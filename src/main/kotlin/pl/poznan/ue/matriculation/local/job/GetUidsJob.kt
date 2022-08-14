package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.UidSearchStartConditions
import pl.poznan.ue.matriculation.local.service.ProcessService

@Component
class GetUidsJob(
    private val processService: ProcessService
) : IJob {
    override val jobType: JobType = JobType.FIND_UIDS

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = UidSearchStartConditions()

    override fun prepare(import: Import) {
        import.importedUids = 0
    }

    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        processService.getUids(importId)
    }

    override fun getCompletionStatus(import: Import): ImportStatus {
        return if (import.saveErrors > 0) {
            ImportStatus.COMPLETED_WITH_ERRORS
        } else {
            ImportStatus.COMPLETE
        }
    }

    override fun getInProgressStatus(): ImportStatus = ImportStatus.SEARCHING_UIDS
}
