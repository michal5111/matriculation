package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.SavePersonsStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

class SavePersonsJob(
    private val processService: ProcessService,
    private val importId: Long,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val importService: ImportService
) : IJob {
    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = SavePersonsStartConditions()

    override fun prepare(import: Import) {
        import.saveErrors = 0
    }

    override fun doWork(import: Import): Import {
        val importDto = importService.get(importId)
        processService.processApplications(
            importId = importId,
            importDto = importDto,
            applicationDtoDataSource = applicationDataSourceFactory.getDataSource(importDto.dataSourceId)
        )
        return importService.get(importId)
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
