package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.applicantDataSources.INotificationSender
import pl.poznan.ue.matriculation.applicantDataSources.IPhotoDownloader
import pl.poznan.ue.matriculation.configuration.LogExecutionTime
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.exception.ApplicationNotFoundException
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.exception.exceptionHandler.ISaveExceptionHandler
import pl.poznan.ue.matriculation.irk.dto.NotificationDto
import pl.poznan.ue.matriculation.kotlinExtensions.retry
import pl.poznan.ue.matriculation.ldap.repo.LdapUserRepository
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import pl.poznan.ue.matriculation.local.dto.IApplicationDto
import pl.poznan.ue.matriculation.local.dto.ImportDtoJpa
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.service.PersonService
import java.util.stream.Stream
import javax.persistence.OptimisticLockException

@Service
class ProcessService(
    private val importRepository: ImportRepository,
    private val applicationRepository: ApplicationRepository,
    private val applicantRepository: ApplicantRepository,
    private val applicantService: ApplicantService,
    private val personService: PersonService,
    private val importProgressRepository: ImportProgressRepository,
    private val ldapUserRepository: LdapUserRepository,
    private val saveExceptionHandler: ISaveExceptionHandler,
    private val asyncService: AsyncService
) {

    val logger: Logger = LoggerFactory.getLogger(ProcessService::class.java)

    @Autowired
    private lateinit var self: ProcessService

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager"
    )
    fun importApplication(
        importId: Long,
        applicationDto: IApplicationDto,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ): Application {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw IllegalStateException("Nie ma aktywnej transakcji")
        }
        val import = importRepository.getById(importId)
        val applicantDto = applicationDtoDataSource.getApplicantById(applicationDto.foreignApplicantId)
        applicationDtoDataSource.preprocess(applicationDto, applicantDto)
        val application = createOrUpdateApplication(applicationDto, applicationDtoDataSource)
        val applicant = createOrUpdateApplicant(applicantDto, applicationDtoDataSource)
        applicantRepository.save(applicant)
        application.applicant = applicant
        application.certificate = applicationDtoDataSource.getPrimaryCertificate(
            applicant = applicant,
            application = application,
            applicantDto = applicantDto,
            applicationDto = applicationDto,
            import = import
        )
        application.import = import
        applicationRepository.save(application)
        import.importProgress.importedApplications++
        return application
    }

    @LogExecutionTime
    private fun createOrUpdateApplication(
        applicationDto: IApplicationDto,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ): Application {
        val foundApplication = applicationRepository.findByForeignIdAndDataSourceId(
            applicationDto.foreignId,
            applicationDtoDataSource.id
        )
        return if (foundApplication != null) {
            applicationDtoDataSource.updateApplication(
                foundApplication,
                applicationDto
            )
        } else {
            applicationDtoDataSource.mapApplicationDtoToApplication(applicationDto).also {
                it.dataSourceId = applicationDtoDataSource.id
                it.editUrl = applicationDtoDataSource.getApplicationEditUrl(it.foreignId)
            }
        }
    }

    @LogExecutionTime
    private fun createOrUpdateApplicant(
        applicantDto: IApplicantDto,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ): Applicant {
        val foundApplicant = applicantRepository.findByForeignIdAndDataSourceId(
            applicantDto.foreignId,
            applicationDtoDataSource.id
        )
        return if (foundApplicant != null) {
            applicationDtoDataSource.updateApplicant(foundApplicant, applicantDto)
        } else {
            applicationDtoDataSource.mapApplicantDtoToApplicant(applicantDto).also {
                it.dataSourceId = applicationDtoDataSource.id
            }
        }
    }

    @LogExecutionTime
    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    fun processApplication(
        importId: Long,
        application: Application,
        importDto: ImportDtoJpa,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ): Person {
        logger.trace("------------------------------------------------Przetwarzam ${application.id}---------------------------------------------")
        val applicant = application.applicant ?: throw ApplicantNotFoundException()
        logger.trace("Sprawdzam aplikanta")
        applicantService.check(applicant)
        logger.trace("Pobieram progres importu")
        val importProgress = importProgressRepository.getById(importId)
        logger.trace("Sprawdzam czy źródło danych implementuje pobieranie zdjęć")
        if (applicationDtoDataSource is IPhotoDownloader) {
            applicant.photo?.let {
                logger.trace("Pobieram zdjęcie")
                applicant.photoByteArrayFuture = asyncService.doAsync {
                    applicationDtoDataSource.getPhoto(it)
                }
            }
        }
        logger.trace("Przetwarzam pobranego aplikanta")
        val personAndStudent = personService.process(
            application = application,
            importDto = importDto,
            postMatriculation = applicationDtoDataSource::postMatriculation
        )
        logger.trace("Przypisuje aplikantowi nadany numer indeksu i usosId")
        application.apply {
            applicant.usosId = personAndStudent.first.id
            applicant.assignedIndexNumber = personAndStudent.second.indexNumber
            importError = null
            stackTrace = null
            importStatus = ApplicationImportStatus.IMPORTED
        }
        logger.trace("Zapisuję aplikanta")
        applicantRepository.save(applicant)
        logger.trace("Zapisuję zgłoszenie")
        applicationRepository.save(application)
        importProgress.savedApplicants++
        logger.trace("------------------------------------------------koniec ${application.id}---------------------------------------------")
        return personAndStudent.first
    }

    @LogExecutionTime
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processApplications(
        importId: Long,
        importDto: ImportDtoJpa,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ): Int {
        var errorCount = 0
        logger.trace("Pobieram strumień zgłoszeń ze statusem nie zaimportowany i błąd")
        val applicationsPage: Stream<Application> = applicationRepository.getAllByImportIdAndImportStatusIn(
            importId,
            listOf(ApplicationImportStatus.NOT_IMPORTED, ApplicationImportStatus.ERROR),
            Sort.by(
                Sort.Order.asc("applicant.name.family"),
                Sort.Order.asc("applicant.name.given"),
                Sort.Order.asc("applicant.name.middle")
            )
        )
        applicationsPage.use { stream ->
            stream.forEach { application ->
                try {
                    var person: Person? = null
                    retry(
                        maxRetry = 5,
                        retryOn = arrayOf(
                            OptimisticLockException::class.java,
                            OptimisticLockingFailureException::class.java,
                            ObjectOptimisticLockingFailureException::class.java,
                        )
                    ) {
                        logger.trace("Próbuję stworzyć/zaktualizować osobę. Próba: {}", it)
                        person = self.processApplication(
                            importId = importId,
                            application = application,
                            importDto = importDto,
                            applicationDtoDataSource = applicationDtoDataSource
                        )
                    }
                    logger.trace("Stworzyłem/zaktualizowałem osobę")
                } catch (e: Exception) {
                    logger.error("Błąd przy tworzeniu lub aktualizowaniu osoby. ", e)
                    errorCount++
                    saveExceptionHandler.handle(e, application, importId)
                }
            }
        }
        return errorCount
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager"
    )
    fun archivePersons(importId: Long) {
        val import = importRepository.getById(importId)
        val applicationStream = applicationRepository.findAllByImportId(importId)
        applicationStream.use {
            it.filter { application ->
                val applicant = application.applicant ?: throw ApplicantNotFoundException()
                applicant.applications.none { applicantApplication ->
                    applicantApplication != application
                        && applicantApplication.import?.importProgress?.importStatus != ImportStatus.ARCHIVED
                }
            }.forEach { application ->
                val applicant = application.applicant ?: throw ApplicationNotFoundException()
                applicantService.clearPersonalData(applicant)
            }
        }
        import.importProgress.importStatus = ImportStatus.ARCHIVED
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun getUids(importId: Long) {
        val applicationStream = applicationRepository.findAllByImportIdStream(importId)
        applicationStream.use {
            it.forEach { application ->
                application.applicant?.let { applicant ->
                    self.getUid(applicant, importId)
                }
            }
        }
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    fun getUid(applicant: Applicant, importId: Long) {
        try {
            val importProgress = importProgressRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
            logger.info("Searching for ldap user for usosId {}", applicant.usosId)
            val ldapUser = applicant.usosId?.let {
                ldapUserRepository.findByUsosId(it)
            } ?: return
            logger.info("Found ldap user {} for usosId {}", ldapUser.uid, applicant.usosId)
            applicant.uid = ldapUser.uid
            logger.info("Apllicant uid is {}", applicant.uid)
            applicantRepository.save(applicant)
            importProgress.importedUids++
        } catch (e: Exception) {
            throw ImportException(importId, "Błąd przy pobieraniu uidów", e)
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun sendNotifications(
        importId: Long,
        notificationSender: INotificationSender
    ) {
        val applicationStream = applicationRepository.findAllByImportIdStream(importId)
        val importProgress = importProgressRepository.findByIdOrNull(importId)
        importProgress?.notificationsSend = 0
        applicationStream.use {
            it.forEach { application ->
                application.applicant?.let { applicant ->
                    self.sendNotification(applicant, importId, notificationSender)
                }
            }
        }
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    fun sendNotification(
        applicant: Applicant,
        importId: Long,
        notificationSender: INotificationSender
    ) {
        try {
            val importProgress = importProgressRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
            val notificationDto = NotificationDto(
                header = "Twoje konto USOS zostało utworzone. / Your USOS account has been created.",
                message = """
            Dzień dobry,

            Twój numer użytkownika – UID (NIU) za pomocą, którego będziesz się logował do systemów uczelnianych takich jak np. USOSweb, poczta uczelniana, to: ${applicant.uid}

            Twoje hasło startowe to numer PESEL połaczony z numerem dokumentu tożsamości podanym podczas rekrutacji na studia w systemie IRK USOS (np. numer dowodu osobistego, paszportu) wpisane łącznie, np. dla osoby o numerze PESEL: 900215222787 i nr dowodu AB425632 początkowe hasło to: 900215222787AB425632

            (w przypadku braku numeru PESEL hasłem startowym jest sam numer dokumentu tożsamości podany podczas rekrutacji na studia w systemie IRK USOS (np. numer paszportu)).

            (w przypadku nieuzupełnionego numeru dokumentu tożsamości w systemie IRK hasłem startowym jest sam numer PESEL).

            Podczas pierwszego logowania do systemu USOSweb system poprosi Cię o zmianę hasła. Zachowaj tę wiadomość bądź zapamiętaj swój numer UID (NIU).

            Na stronie logowania istnieje również możliwość zresetowania hasła poprzez wysłanie linku na adres e-mail podany podczas rekrutacji.

            W wypadku problemów z logowaniem prosimy o kontakt – helpdesk@ue.poznan.pl

            Pozdrawiamy!

            Zespół Centrum Informatyki UEP

            Dear Student,

            Your login number (called UID or NIU) used to register to PUEB IT systems is: ${applicant.uid}

            Your passport number used in the application system is your temporary password to the system.

            When you log in for the first time to USOSweb  the system will ask to change the password.

            Please keep this message or remember your login number (UID/NIU)

            If you have any problems logging in please contact us at: helpdesk@ue.poznan.pl

            with best regards

            PUEB IT Centre
                """.trimIndent()
            )
            notificationSender.sendNotification(applicant.foreignId, notificationDto)
            importProgress.notificationsSend++
        } catch (e: Exception) {
            throw ImportException(importId, "Błąd przy wysyłaniu powiadomień", e)
        }
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    fun findPotentialDuplicate(applicant: Applicant, importId: Long) {
        val importProgress = importProgressRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
        val dateOfBirth = applicant.basicData.dateOfBirth ?: throw IllegalArgumentException("Date of birth is null")
        val potentialDuplicatesList = personService.findPotentialDuplicate(
            name = applicant.name.given,
            surname = applicant.name.family,
            birthDate = dateOfBirth,
            idNumbers = applicant.identityDocuments.map {
                it.number ?: throw IllegalArgumentException("Identity document number is null")
            }
        )
        if (potentialDuplicatesList.isNotEmpty()) {
            logger.warn("Wykryto potencjalny duplikat!")
            applicant.potentialDuplicateStatus = DuplicateStatus.POTENTIAL_DUPLICATE
            importProgress.potentialDuplicates++
        } else {
            applicant.potentialDuplicateStatus = DuplicateStatus.OK
        }
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager",
        readOnly = true
    )
    fun findPotentialDuplicates(importId: Long) {
        val applicationsStream =
            applicationRepository.findAllByImportIdStreamAndApplicantPotentialDuplicateStatus(
                importId,
                mutableListOf(DuplicateStatus.NOT_CHECKED, DuplicateStatus.POTENTIAL_DUPLICATE)
            )
        applicationsStream.use { stream ->
            stream.forEach {
                val applicant = it.applicant ?: throw ApplicantNotFoundException()
                self.findPotentialDuplicate(applicant, importId)
            }
        }
    }
}
