package pl.ue.poznan.matriculation.irk.domain.applicants


import com.fasterxml.jackson.annotation.JsonProperty

data class EducationData(
        val documents: List<Document>,
        @JsonProperty("high_school_city")
        val highSchoolCity: String?,
        @JsonProperty("high_school_name")
        val highSchoolName: String?,
        @JsonProperty("high_school_type")
        val highSchoolType: String?,
        @JsonProperty("high_school_usos_code")
        val highSchoolUsosCode: String?
)