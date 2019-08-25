package pl.ue.poznan.matriculation.irk.domain.applications


import com.fasterxml.jackson.annotation.JsonProperty

data class ForeignerData(
    @JsonProperty("base_of_stay")
    val baseOfStay: String?,
    @JsonProperty("basis_of_admission")
    val basisOfAdmission: String?,
    @JsonProperty("source_of_financing")
    val sourceOfFinancing: String?
)