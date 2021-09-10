package pl.poznan.ue.matriculation.irk.dto.applicants


import com.fasterxml.jackson.annotation.JsonProperty

data class EducationDataDTO(
    var documents: List<DocumentDTO> = listOf(),
    @JsonProperty("high_school_city")
    val highSchoolCity: String?,
    @JsonProperty("high_school_name")
    val highSchoolName: String?,
    @JsonProperty("high_school_type")
    val highSchoolType: String?,
    @JsonProperty("high_school_usos_code")
    val highSchoolUsosCode: Long?
)
