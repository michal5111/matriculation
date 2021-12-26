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
        import.importStatus = ImportStatus.SAVING
    }

    override fun doWork() {
        val importDto = importService.get(importId)
        val errorsCount = processService.processApplications(
            importId = importId,
            importDto = importDto,
            applicationDtoDataSource = applicationDataSourceFactory.getDataSource(importDto.dataSourceId)
        )
        if (errorsCount > 0) {
            importService.get(importId).apply {
                importStatus = ImportStatus.COMPLETED_WITH_ERRORS
            }.let {
                importService.save(it)
            }
        } else {
            importService.get(importId).apply {
                importStatus = ImportStatus.COMPLETE
            }.let {
                importService.save(it)
            }
        }
    }
}
