package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.ArchivePersonsStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.ProcessService

class ArchiveApplicationsJob(
    private val processService: ProcessService,
    private val importId: Long
) : IJob {
    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = ArchivePersonsStartConditions()

    override fun prepare(import: Import) {
        TODO("Not yet implemented")
    }

    override fun doWork() {
        processService.archivePersons(importId)
    }
}