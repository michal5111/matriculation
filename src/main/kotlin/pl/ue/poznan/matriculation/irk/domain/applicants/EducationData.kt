package pl.ue.poznan.matriculation.irk.domain.applicants


import com.fasterxml.jackson.annotation.JsonProperty
import pl.ue.poznan.matriculation.irk.dto.applicants.DocumentDTO

data class EducationData(
        val documentDTOS: List<DocumentDTO>,
        @JsonProperty("high_school_city")
        val highSchoolCity: String?,
        @JsonProperty("high_school_name")
        val highSchoolName: String?,
        @JsonProperty("high_school_type")
        val highSchoolType: String?,
        @JsonProperty("high_school_usos_code")
        val highSchoolUsosCode: String?
)