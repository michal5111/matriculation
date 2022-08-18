package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ApplicationNotFoundException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.ArchivePersonsStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicantService
import pl.poznan.ue.matriculation.local.service.ApplicationService

@Component
class ArchiveApplicationsJob(
    private val applicationService: ApplicationService,
    private val applicantService: ApplicantService
) : IJob {
    override val jobType: JobType = JobType.ARCHIVE

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = ArchivePersonsStartConditions()

    override fun prepare(import: Import) {
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager"
    )
    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        val applicationStream = applicationService.findAllForArchive(importId)
        applicationStream.use {
            it.forEach { application ->
                val applicant = application.applicant ?: throw ApplicationNotFoundException()
                application.certificate = null
                applicantService.anonymize(applicant)
            }
        }
    }

    override fun getCompletionStatus(import: Import): ImportStatus = ImportStatus.ARCHIVED

    override fun getInProgressStatus(): ImportStatus = ImportStatus.STARTED
}
