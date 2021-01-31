package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.exceptionHandler.ISaveExceptionHandler
import pl.poznan.ue.matriculation.ldap.repo.LdapUserRepository
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
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
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
class ProcessService(
    private val importRepository: ImportRepository,
    private val applicationRepository: ApplicationRepository,
    private val applicantRepository: ApplicantRepository,
    private val applicantService: ApplicantService,
    private val personService: PersonService,
    private val importProgressRepository: ImportProgressRepository,
    private val ldapLdapUserRepository: LdapUserRepository,
    private val saveExceptionHandler: ISaveExceptionHandler
) {

    val logger: Logger = LoggerFactory.getLogger(ProcessService::class.java)

    @Autowired
    private lateinit var self: ProcessService

    @PersistenceContext(unitName = "oracle")
    private lateinit var oracleEntityManager: EntityManager

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager"
    )
    fun processApplication(
        importId: Long,
        applicationDto: IApplicationDto,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ): Application {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw IllegalStateException("Nie ma aktywnej transakcji")
        }
        val import = importRepository.getOne(importId)
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
        applicationRepository.save(application)
        application.import = import
        import.importProgress.importedApplications++
        return application
    }

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
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw IllegalStateException("Nie ma aktywnej transakcji")
        }
        applicantService.check(application.applicant!!)
        val importProgress = importProgressRepository.getOne(importId)
        application.applicant!!.photo?.let {
            application.applicant!!.photoByteArray = applicationDtoDataSource.getPhoto(it)
        }
        val personAndStudent = personService.process(
            application = application,
            dateOfAddmision = importDto.dateOfAddmision,
            didacticCycleCode = importDto.didacticCycleCode,
            indexPoolCode = importDto.indexPoolCode,
            programmeCode = importDto.programmeCode,
            registration = importDto.registration,
            stageCode = importDto.stageCode,
            startDate = importDto.startDate,
            postMatriculation = applicationDtoDataSource::postMatriculation
        )
        application.apply {
            applicant!!.usosId = personAndStudent.first.id
            applicant!!.assignedIndexNumber = personAndStudent.second.indexNumber
            importError = null
            stackTrace = null
            importStatus = ApplicationImportStatus.IMPORTED
        }
        applicantRepository.save(application.applicant!!)
        applicationRepository.save(application)
        importProgress.savedApplicants++
        return personAndStudent.first
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processApplications(
        importId: Long,
        importDto: ImportDtoJpa,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ): Int {
        var errorCount = 0
        val applicationsPage: Stream<Application> =
            applicationRepository.getAllByImportIdAndApplicationImportStatus(importId)
        applicationsPage.use {
            it.forEach { application ->
                try {
                    val person = self.processApplication(
                        importId = importId,
                        application = application,
                        importDto = importDto,
                        applicationDtoDataSource = applicationDtoDataSource
                    )
                    oracleEntityManager.detach(person)
                } catch (e: Exception) {
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
        val applicationStream = applicationRepository.findAllByImportId(importId)
        applicationStream.use {
            it.filter { application ->
                application.applicant!!.applications.none { applicantApplication ->
                    applicantApplication != application
                            && applicantApplication.import?.importProgress?.importStatus != ImportStatus.ARCHIVED
                }
            }.forEach { application ->
                applicantService.clearPersonalData(application.applicant!!)
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun getUids(importId: Long) {
        val applicationStream = applicationRepository.findAllByImportIdStream(importId)
        val importProgress = importProgressRepository.findByIdOrNull(importId)
        importProgress?.importedUids = 0
        applicationStream.use {
            it.forEach { application ->
                application.applicant?.let { applicant ->
                    getUid(applicant, importId)
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
            val importProgress = importProgressRepository.findByIdOrNull(importId)
            logger.info("Searching for ldap user for usosId {}", applicant.usosId)
            val ldapUser = applicant.usosId?.let {
                ldapLdapUserRepository.findByUsosId(it)
            } ?: return
            logger.info("Found ldap user {} for usosId {}", ldapUser.uid, applicant.usosId)
            applicant.uid = ldapUser.uid
            logger.info("Apllicant uid is {}", applicant.uid)
            applicantRepository.save(applicant)
            importProgress!!.importedUids++
        } catch (e: Exception) {
            throw ImportException(importId, "Błąd przy pobieraniu uidów", e)
        }
    }
}