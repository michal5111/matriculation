package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.CheckForPotentialDuplicatesStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.ProcessService

class CheckForPotentialDuplicatesJob(
    private val processService: ProcessService,
    private val importId: Long
) : IJob {
    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = CheckForPotentialDuplicatesStartConditions()

    override fun prepare(import: Import) {
        import.potentialDuplicates = 0
    }

    override fun doWork(import: Import): Import {
        processService.findPotentialDuplicates(importId)
        return import
    }

    override fun getCompletionStatus(import: Import): ImportStatus {
        return ImportStatus.IMPORTED
    }

    override fun getInProgressStatus(): ImportStatus {
        return ImportStatus.CHECKING_POTENTIAL_DUPLICATES
    }
}
