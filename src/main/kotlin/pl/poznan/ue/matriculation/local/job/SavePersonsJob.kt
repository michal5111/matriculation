package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.SavePersonsStartConditions
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

class SavePersonsJob(
    private val importRepository: ImportRepository,
    private val processService: ProcessService,
    private val importId: Long,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val importService: ImportService
) : IJob {
    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = SavePersonsStartConditions()

    override fun prepare(import: Import) {
        import.importProgress.saveErrors = 0
        import.importProgress.importStatus = ImportStatus.SAVING
    }

    override fun doWork() {
        val importDto = importRepository.getDtoById(importId)
        val errorsCount = processService.processApplications(
            importId = importId,
            importDto = importDto,
            applicationDtoDataSource = applicationDataSourceFactory.getDataSource(importDto.dataSourceId)
        )
        if (errorsCount > 0) {
            importService.getProgress(importId).apply {
                importStatus = ImportStatus.COMPLETED_WITH_ERRORS
            }.let {
                importService.saveProgress(it)
            }
        } else {
            importService.getProgress(importId).apply {
                importStatus = ImportStatus.COMPLETE
            }.let {
                importService.saveProgress(it)
            }
        }
    }
}
