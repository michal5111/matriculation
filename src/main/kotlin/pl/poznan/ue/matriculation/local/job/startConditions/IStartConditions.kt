package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.local.domain.import.Import

interface IStartConditions {
    fun canStart(import: Import)
}