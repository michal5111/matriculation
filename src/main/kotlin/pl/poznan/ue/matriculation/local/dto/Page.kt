package pl.poznan.ue.matriculation.local.dto

data class Page<T>(
    val content: Collection<T>,
    val number: Int,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int
)
