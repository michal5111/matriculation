package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.CheckForPotentialDuplicatesStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.ProcessService

@Component
class CheckForPotentialDuplicatesJob(
    private val processService: ProcessService,
) : IJob {
    override val jobType: JobType = JobType.CHECK_FOR_POTENTIAL_DUPLICATES

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = CheckForPotentialDuplicatesStartConditions()

    override fun prepare(import: Import) {
        import.potentialDuplicates = 0
    }

    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        processService.findPotentialDuplicates(importId)
    }

    override fun getCompletionStatus(import: Import): ImportStatus = ImportStatus.IMPORTED

    override fun getInProgressStatus(): ImportStatus = ImportStatus.CHECKING_POTENTIAL_DUPLICATES
}
