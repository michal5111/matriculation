package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.CheckForPotentialDuplicatesStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationService
import pl.poznan.ue.matriculation.local.service.PotentialDuplicateFinder

@Component
class CheckForPotentialDuplicatesJob(
    private val applicationService: ApplicationService,
    private val potentialDuplicateFinder: PotentialDuplicateFinder
) : IJob {
    override val jobType: JobType = JobType.CHECK_FOR_POTENTIAL_DUPLICATES

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = CheckForPotentialDuplicatesStartConditions()

    override fun prepare(import: Import) {
        import.potentialDuplicates = 0
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager",
        readOnly = true
    )
    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        val applicationsStream = applicationService.findAllStreamByImportIdAndApplicantPotentialDuplicateStatusIn(
            importId,
            listOf(DuplicateStatus.NOT_CHECKED, DuplicateStatus.POTENTIAL_DUPLICATE)
        )
        applicationsStream.use { stream ->
            stream.forEach {
                val applicant = it.applicant ?: throw ApplicantNotFoundException()
                potentialDuplicateFinder.findPotentialDuplicate(applicant, importId)
            }
        }
    }

    override fun getCompletionStatus(import: Import): ImportStatus = ImportStatus.IMPORTED

    override fun getInProgressStatus(): ImportStatus = ImportStatus.CHECKING_POTENTIAL_DUPLICATES
}
