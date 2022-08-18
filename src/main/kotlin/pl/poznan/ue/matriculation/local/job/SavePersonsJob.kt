package pl.poznan.ue.matriculation.local.job

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.Sort
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.exception.exceptionHandler.SaveExceptionHandler
import pl.poznan.ue.matriculation.kotlinExtensions.retry
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.SavePersonsStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ApplicationProcessor
import pl.poznan.ue.matriculation.local.service.ApplicationService
import java.util.stream.Stream
import javax.persistence.OptimisticLockException

@Component
class SavePersonsJob(
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val applicationService: ApplicationService,
    private val applicationProcessor: ApplicationProcessor,
    private val saveExceptionHandler: SaveExceptionHandler
) : IJob {
    override val jobType: JobType = JobType.SAVE

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = SavePersonsStartConditions()

    override fun prepare(import: Import) {
        import.saveErrors = 0
    }

    val logger: Logger = LoggerFactory.getLogger(SavePersonsJob::class.java)

    @LogExecutionTime
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        val applicationDtoDataSource = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        logger.trace("Pobieram strumień zgłoszeń ze statusem nie zaimportowany i błąd")
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
                        logger.trace("Próbuję stworzyć/zaktualizować osobę. Próba: {}", it)
                        applicationProcessor.processApplication(
                            importId = importId,
                            application = application,
                            applicationDtoDataSource = applicationDtoDataSource
                        )
                    }
                    logger.trace("Stworzyłem/zaktualizowałem osobę")
                } catch (e: Exception) {
                    logger.error("Błąd przy tworzeniu lub aktualizowaniu osoby.", e)
                    saveExceptionHandler.handle(e, application, importId)
                }
            }
        }
    }

    override fun getCompletionStatus(import: Import): ImportStatus {
        return if (import.saveErrors > 0) {
            ImportStatus.COMPLETED_WITH_ERRORS
        } else {
            ImportStatus.COMPLETE
        }
    }

    override fun getInProgressStatus(): ImportStatus {
        return ImportStatus.SAVING
    }
}
