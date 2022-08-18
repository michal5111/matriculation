package pl.poznan.ue.matriculation.local.dto

data class ProcessResult<T>(
    val systemId: Long,
    val assignedIndexNumber: String,
    val person: T
)
