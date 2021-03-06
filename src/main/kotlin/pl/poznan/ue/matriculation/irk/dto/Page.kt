package pl.poznan.ue.matriculation.irk.dto

data class Page<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)