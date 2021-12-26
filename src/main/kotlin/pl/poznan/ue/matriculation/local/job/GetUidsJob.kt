package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.UidSearchStartConditions
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

class GetUidsJob(
    private val processService: ProcessService,
    private val importService: ImportService,
    private val importId: Long
) : IJob {
    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = UidSearchStartConditions()

    override fun prepare(import: Import) {
        import.importedUids = 0
        import.importStatus = ImportStatus.SEARCHING_UIDS
    }

    override fun doWork() {
        processService.getUids(importId)
        val import = importService.get(importId)
        if (import.saveErrors > 0) {
            import.importStatus = ImportStatus.COMPLETED_WITH_ERRORS
        } else {
            import.importStatus = ImportStatus.COMPLETE
        }
        importService.save(import)
    }
}
