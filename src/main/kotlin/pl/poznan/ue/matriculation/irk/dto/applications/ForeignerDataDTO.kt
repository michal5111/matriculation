package pl.poznan.ue.matriculation.irk.dto.applications


import com.fasterxml.jackson.annotation.JsonProperty

data class ForeignerDataDTO(
        @JsonProperty("base_of_stay")
        val baseOfStay: String?,
        @JsonProperty("basis_of_admission")
        val basisOfAdmission: String?,
        @JsonProperty("source_of_financing")
        val sourceOfFinancing: String?
)