package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions

interface IJob {

    var status: JobStatus

    val startCondition: IStartConditions

    fun prepare(import: Import)

    fun doWork()
}