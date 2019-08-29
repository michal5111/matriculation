package pl.ue.poznan.matriculation.irk.domain

data class Page<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>?
)