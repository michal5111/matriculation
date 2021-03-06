package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.Sort
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.applicantDataSources.INotificationSender
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.exception.ApplicationNotFoundException
import pl.poznan.ue.matriculation.exception.exceptionHandler.ISaveExceptionHandler
import pl.poznan.ue.matriculation.kotlinExtensions.retry
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import pl.poznan.ue.matriculation.local.dto.IApplicationDto
import java.util.stream.Stream
import javax.persistence.OptimisticLockException

@Service
class ProcessService(
    private val importService: ImportService,
    private val applicationService: ApplicationService,
    private val applicantService: ApplicantService,
    private val saveExceptionHandler: ISaveExceptionHandler,
    private val uidService: UidService,
    private val notificationService: NotificationService,
    private val potentialDuplicateFinder: PotentialDuplicateFinder,
    private val applicationProcessor: ApplicationProcessor
) {

    val logger: Logger = LoggerFactory.getLogger(ProcessService::class.java)

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
        val import = importService.get(importId)
        val applicantDto = applicationDtoDataSource.getApplicantById(applicationDto.foreignApplicantId, applicationDto)
        var application = createOrUpdateApplication(applicationDto, applicationDtoDataSource)
        var applicant = createOrUpdateApplicant(applicantDto, applicationDtoDataSource, applicationDto)
        applicant.primaryIdentityDocument = applicationDtoDataSource.getPrimaryIdentityDocument(
            applicant = applicant,
            application = application,
            applicantDto = applicantDto,
            applicationDto = applicationDto,
            import = import
        )
        applicant = applicantService.save(applicant)
        application.applicant = applicant
        application.certificate = applicationDtoDataSource.getPrimaryCertificate(
            applicant = applicant,
            application = application,
            applicantDto = applicantDto,
            applicationDto = applicationDto,
            import = import
        )
        application.import = import
        application = applicationService.save(application)
        import.importedApplications++
        return application
    }

    @LogExecutionTime
    private fun createOrUpdateApplication(
        applicationDto: IApplicationDto,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ): Application {
        val foundApplication = applicationService.findByForeignIdAndDataSourceId(
            applicationDto.foreignId,
            applicationDtoDataSource.id
        )
        return if (foundApplication != null) {
            applicationDtoDataSource.updateApplication(foundApplication, applicationDto)
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
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>,
        applicationDto: IApplicationDto
    ): Applicant {
        val foundApplicant = applicantService.findByForeignIdAndDataSourceId(
            applicantDto.foreignId,
            applicationDtoDataSource.id
        )
        return if (foundApplicant != null) {
            applicationDtoDataSource.updateApplicant(foundApplicant, applicantDto, applicationDto)
        } else {
            applicationDtoDataSource.mapApplicantDtoToApplicant(applicantDto).also {
                it.dataSourceId = applicationDtoDataSource.id
            }
        }
    }

    @LogExecutionTime
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processApplications(
        importId: Long,
        import: Import,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ) {
        logger.trace("Pobieram strumie?? zg??osze?? ze statusem nie zaimportowany i b????d")
        val applicationsPage: Stream<Application> = applicationService.getAllByImportIdAndImportStatusIn(
            importId,
            listOf(ApplicationImportStatus.NOT_IMPORTED, ApplicationImportStatus.ERROR),
            Sort.by(
                Sort.Order.asc("applicant.family"),
                Sort.Order.asc("applicant.given"),
                Sort.Order.asc("applicant.middle")
            )
        )
        applicationsPage.use { stream ->
            stream.forEach { application ->
                try {
                    retry(
                        maxRetry = 5,
                        retryOn = arrayOf(
                            OptimisticLockException::class.java,
                            OptimisticLockingFailureException::class.java,
                            ObjectOptimisticLockingFailureException::class.java,
                        )
                    ) {
                        logger.trace("Pr??buj?? stworzy??/zaktualizowa?? osob??. Pr??ba: {}", it)
                        applicationProcessor.processApplication(
                            importId = importId,
                            application = application,
                            importDto = import,
                            applicationDtoDataSource = applicationDtoDataSource
                        )
                    }
                    logger.trace("Stworzy??em/zaktualizowa??em osob??")
                } catch (e: Exception) {
                    logger.error("B????d przy tworzeniu lub aktualizowaniu osoby.", e)
                    saveExceptionHandler.handle(e, application, importId)
                }
            }
        }
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager"
    )
    fun archivePersons(importId: Long) {
        val applicationStream = applicationService.findAllForArchive(importId)
        applicationStream.use {
            it.forEach { application ->
                val applicant = application.applicant ?: throw ApplicationNotFoundException()
                application.certificate = null
                applicantService.anonymize(applicant)
            }
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun getUids(importId: Long) {
        val applicationStream = applicationService.findAllStreamByImportId(importId)
        applicationStream.use {
            it.forEach { application ->
                application.applicant?.let { applicant ->
                    uidService.get(applicant, importId)
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun sendNotifications(
        importId: Long,
        notificationSender: INotificationSender
    ) {
        val applicationStream = applicationService.findAllByImportIdAndNotificationSent(importId, false)
        applicationStream.use {
            it.forEach { application ->
                notificationService.sendNotification(application, importId, notificationSender)
            }
        }
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager",
        readOnly = true
    )
    fun findPotentialDuplicates(importId: Long) {
        val applicationsStream = applicationService.findAllStreamByImportIdAndApplicantPotentialDuplicateStatusIn(
            importId,
            listOf(DuplicateStatus.NOT_CHECKED, DuplicateStatus.POTENTIAL_DUPLICATE)
        )
        applicationsStream.use { stream ->
            stream.forEach {
                val applicant = it.applicant ?: throw ApplicantNotFoundException()
                potentialDuplicateFinder.findPotentialDuplicate(applicant, importId)
            }
        }
    }
}
