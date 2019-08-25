package pl.ue.poznan.matriculation.irk.domain

data class Page(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Any>?
)