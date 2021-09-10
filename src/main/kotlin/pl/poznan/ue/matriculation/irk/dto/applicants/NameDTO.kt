package pl.poznan.ue.matriculation.irk.dto.applicants

data class NameDTO(
    val middle: String?,
    val family: String,
    val given: String,
    val maiden: String?
)