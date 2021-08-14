package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.UidSearchStartConditions
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

class GetUidsJob(
    private val processService: ProcessService,
    private val importService: ImportService,
    private val importRepository: ImportRepository,
    private val importId: Long
) : IJob {
    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = UidSearchStartConditions()

    override fun prepare(import: Import) {
        import.importProgress.importedUids = 0
        import.importProgress.importStatus = ImportStatus.SEARCHING_UIDS
    }

    override fun doWork() {
        processService.getUids(importId)
        val import = importRepository.getById(importId)
        if (import.importProgress.saveErrors > 0) {
            importService.setImportStatus(ImportStatus.COMPLETED_WITH_ERRORS, importId)
        } else {
            importService.setImportStatus(ImportStatus.COMPLETE, importId)
        }
    }
}