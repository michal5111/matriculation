package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.ArchivePersonsStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.ProcessService

@Component
class ArchiveApplicationsJob(
    private val processService: ProcessService
) : IJob {
    override val jobType: JobType = JobType.ARCHIVE

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = ArchivePersonsStartConditions()

    override fun prepare(import: Import) {
    }

    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        processService.archivePersons(importId)
    }

    override fun getCompletionStatus(import: Import): ImportStatus = ImportStatus.ARCHIVED

    override fun getInProgressStatus(): ImportStatus = ImportStatus.STARTED
}
