package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.CheckForPotentialDuplicatesStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

class CheckForPotentialDuplicatesJob(
    private val processService: ProcessService,
    private val importId: Long,
    private val importService: ImportService
) : IJob {
    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = CheckForPotentialDuplicatesStartConditions()

    override fun prepare(import: Import) {
        import.potentialDuplicates = 0
        import.importStatus = ImportStatus.CHECKING_POTENTIAL_DUPLICATES
        importService.save(import)
    }

    override fun doWork() {
        processService.findPotentialDuplicates(importId)
        importService.setImportStatus(ImportStatus.IMPORTED, importId)
    }
}
