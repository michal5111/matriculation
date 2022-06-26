package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions

interface IJob {

    val jobType: JobType

    var status: JobStatus

    val startCondition: IStartConditions

    fun prepare(import: Import)

    fun doWork(import: Import)

    fun getCompletionStatus(import: Import): ImportStatus

    fun getInProgressStatus(): ImportStatus
}
