package pl.poznan.ue.matriculation.local.job.startConditions

sealed class StateTransitionResult

data class StateTransitionFailure(
    val message: String
) : StateTransitionResult()

object StateTransitionSuccess : StateTransitionResult()

