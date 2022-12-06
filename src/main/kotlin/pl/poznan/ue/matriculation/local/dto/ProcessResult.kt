package pl.poznan.ue.matriculation.local.dto

data class ProcessResult<T>(
    val systemId: Long,
    var assignedIndexNumber: String?,
    val person: T
)
