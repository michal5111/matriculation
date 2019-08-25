package pl.ue.poznan.matriculation.irk.domain.applicants

data class Name(
        val middle: String?,
        val family: String?,
        val given: String?,
        val maiden: String?
)