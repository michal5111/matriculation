package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.UidSearchStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationService
import pl.poznan.ue.matriculation.local.service.UidService

@Component
class GetUidsJob(
    private val applicationService: ApplicationService,
    private val uidService: UidService
) : IJob {
    override val jobType: JobType = JobType.FIND_UIDS

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = UidSearchStartConditions()

    override fun prepare(import: Import) {
        import.importedUids = 0
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        val applicationStream = applicationService.findAllStreamByImportId(importId)
        applicationStream.use {
            it.forEach { application ->
                application.applicant?.let { applicant ->
                    uidService.get(applicant, importId)
                }
            }
        }
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
