package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.SavePersonsStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ProcessService

@Component
class SavePersonsJob(
    private val processService: ProcessService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory
) : IJob {
    override val jobType: JobType = JobType.SAVE

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = SavePersonsStartConditions()

    override fun prepare(import: Import) {
        import.saveErrors = 0
    }

    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        processService.processApplications(
            importId = importId,
            applicationDtoDataSource = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        )
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
