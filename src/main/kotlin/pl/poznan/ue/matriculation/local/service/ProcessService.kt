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
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
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
        val import = importRepository.getOne(importId)
        val applicantDto = applicationDtoDataSource.getApplicantById(applicationDto.getForeignApplicantId())
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

    private fun createOrUpdateApplication(applicationDto: AbstractApplicationDto, applicationDtoDataSource: IApplicationDataSource<AbstractApplicationDto, AbstractApplicantDto>): Application {
        val foundApplication = applicationRepository.findByForeignIdAndDataSourceId(
                applicationDto.getForeignId(),
                applicationDtoDataSource.getId()
        )
        return if (foundApplication != null) {
            applicationDtoDataSource.updateApplication(
                    foundApplication,
                    applicationDto
            )
        } else {
            applicationDtoDataSource.mapApplicationDtoToApplication(applicationDto).also {
                it.dataSourceId = applicationDtoDataSource.getId()
                it.editUrl = applicationDtoDataSource.getApplicationEditUrl(it.foreignId)
            }
        }
    }

    private fun createOrUpdateApplicant(applicantDto: AbstractApplicantDto, applicationDtoDataSource: IApplicationDataSource<AbstractApplicationDto, AbstractApplicantDto>): Applicant {
        val foundApplicant = applicantRepository.findByForeignIdAndDataSourceId(
                applicantDto.getForeignId(),
                applicationDtoDataSource.getId()
        )
        return if (foundApplicant != null) {
            applicationDtoDataSource.updateApplicant(foundApplicant, applicantDto)
        } else {
            applicationDtoDataSource.mapApplicantDtoToApplicant(applicantDto).also {
                it.dataSourceId = applicationDtoDataSource.getId()
            }
        }
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
            applicationDtoDataSource.postMatriculation(application.id!!)
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