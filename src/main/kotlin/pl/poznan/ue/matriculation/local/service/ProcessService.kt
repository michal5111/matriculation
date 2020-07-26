package pl.poznan.ue.matriculation.local.service

import org.hibernate.JDBCException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.kotlinExtensions.stackTraceToString
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.dto.AbstractApplicantDto
import pl.poznan.ue.matriculation.local.dto.AbstractApplicationDto
import pl.poznan.ue.matriculation.local.dto.ImportDtoJpa
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.service.PersonService
import java.lang.reflect.UndeclaredThrowableException
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
        private val importProgressRepository: ImportProgressRepository
) {

    val logger: Logger = LoggerFactory.getLogger(ProcessService::class.java)

    @Autowired
    private lateinit var self: ProcessService

    @PersistenceContext(unitName = "oracle")
    private lateinit var oracleEntityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processApplication(importId: Long, applicationDto: AbstractApplicationDto, applicationDtoDataSource: IApplicationDataSource<AbstractApplicationDto, AbstractApplicantDto>): Application {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw IllegalStateException("Nie ma aktywnej transakcji")
        }
        logger.debug("Pobieram import...")
        val import = importRepository.getOne(importId)
        val applicantDto = applicationDtoDataSource.getApplicantById(applicationDto.getForeignApplicantId())
        applicationDtoDataSource.preprocess(applicationDto, applicantDto)
        logger.debug("Pobrałem import... Sprawdzam czy istnieje już aplikacja o takim foreignId i foreignIdType")
        val application = if (applicationRepository.existsByForeignIdAndDatasourceId(
                        applicationDto.getForeignId(),
                        applicationDtoDataSource.getId())
        ) {
            logger.debug("Istnieje aktualizuję...")
            applicationDtoDataSource.updateApplication(
                    applicationRepository.getByForeignIdAndDatasourceId(
                            applicationDto.getForeignId(),
                            applicationDtoDataSource.getId()
                    ),
                    applicationDto
            )
        } else {
            logger.debug("Nie istnieje dodaję...")
            applicationDtoDataSource.mapApplicationDtoToApplication(applicationDto).also {
                it.datasourceId = applicationDtoDataSource.getId()
            }
        }
        logger.debug("Sprawdzam czy istnieje już aplikant o takim foreignId i foreignIdType")
        val applicant = applicantDto.let { abstractApplicantDto ->
            val applicant = applicantRepository.findByForeignIdAndDatasourceId(
                    abstractApplicantDto.getForeignId(),
                    applicationDtoDataSource.getId()
            )
            if (applicant != null) {
                logger.debug("Istnieje aktualizuję...")
                return@let applicationDtoDataSource.updateApplicant(applicant, abstractApplicantDto)
            } else {
                logger.debug("Nie istnieje dodaję...")
                return@let applicationDtoDataSource.mapApplicantDtoToApplicant(abstractApplicantDto).also {
                    it.datasourceId = applicationDtoDataSource.getId()
                }
            }
        }
        logger.debug("zapisuję aplikanta")
        applicantRepository.save(applicant)
        application.applicant = applicant
        logger.debug("Zapisałem aplikanta")

//        if (!applicant.applications.any { it.irkId == application.irkId }) {
//            application.applicant = applicant
//            applicant.applications.add(application)
//        }

        application.certificate = applicationDtoDataSource.getPrimaryCertificate(
                application.foreignId,
                applicant.educationData.documents
        )
        //application.certificate?.Applications?.add(application)
        logger.debug("zapisuję aplikację")
        applicationRepository.save(application)

//        if (!applicationRepository.existsByImportIdAndIrkId(importId, application.irkId)) {
//            import.applications.add(application)
//            application.import = import
//        }

        application.import = import

        logger.debug("zwiększam liczbę zaimportowanych")
        import.importProgress!!.importedApplications++
        return application
    }

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun processPerson(
            importId: Long,
            application: Application,
            importDto: ImportDtoJpa,
            applicationDtoDataSource: IApplicationDataSource<AbstractApplicationDto, AbstractApplicantDto>
    ): Person {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw IllegalStateException("Nie ma aktywnej transakcji")
        }
        val importProgress = importProgressRepository.getOne(importId)
        application.applicant!!.photo?.let {
            application.applicant!!.photoByteArray = applicationDtoDataSource.getPhoto(it)
        }
        applicantService.check(application.applicant!!)
        val personAndAssignedNumber = personService.processPerson(
                application = application,
                dateOfAddmision = importDto.dateOfAddmision,
                didacticCycleCode = importDto.didacticCycleCode,
                indexPoolCode = importDto.indexPoolCode,
                programmeCode = importDto.programmeCode,
                registration = importDto.registration,
                stageCode = importDto.stageCode,
                startDate = importDto.startDate
        ) {
            applicationDtoDataSource.postMatriculation(applicationId = application.id!!, irkApplication = it)
        }
        personAndAssignedNumber.let { pair ->
            application.applicant!!.usosId = pair.first.id
            application.applicant!!.assignedIndexNumber = pair.second
        }
        application.importError = null
        application.stackTrace = null
        application.importStatus = ApplicationImportStatus.IMPORTED
        applicantRepository.save(application.applicant!!)
        applicationRepository.save(application)
        importProgress.savedApplicants++
        return personAndAssignedNumber.first
    }

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun handleSaveException(exception: Exception, application: Application, importId: Long) {
        val importProgress = importProgressRepository.getOne(importId)
        application.importError = ""
        var e: Throwable? = exception
        do {
            if (e is UndeclaredThrowableException) {
                e = e.cause
            }
            if (e is JDBCException) {
                application.importError += "${e.javaClass.simpleName}: ${e.message} Error code: ${e.errorCode} " +
                        "Sql: ${e.sql} " +
                        "Sql state: ${e.sqlState} "
            } else {
                application.importError += "${e?.javaClass?.simpleName}: ${e?.message} "
            }
            e = e?.cause
        } while (e != null)
        application.importStatus = ApplicationImportStatus.ERROR
        application.stackTrace = exception.stackTraceToString()
        importProgress.saveErrors++
        applicantRepository.save(application.applicant!!)
        applicationRepository.save(application)
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processPersons(
            importId: Long,
            importDto: ImportDtoJpa,
            applicationDtoDataSource: IApplicationDataSource<AbstractApplicationDto, AbstractApplicantDto>
    ): Int {
        var errorCount = 0
        val applicationsPage: Stream<Application> = applicationRepository.getAllByImportIdAndApplicationImportStatus(importId)
        applicationsPage.use {
            it.forEach { application ->
                try {
                    val person = self.processPerson(
                            importId = importId,
                            application = application,
                            importDto = importDto,
                            applicationDtoDataSource = applicationDtoDataSource
                    )
                    oracleEntityManager.detach(person)
                } catch (e: Exception) {
                    errorCount++
                    self.handleSaveException(e, application, importId)
                }
            }
        }
        return errorCount
    }

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
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
}