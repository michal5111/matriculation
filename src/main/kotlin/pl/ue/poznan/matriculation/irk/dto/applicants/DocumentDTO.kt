package pl.ue.poznan.matriculation.irk.dto.applicants


import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class DocumentDTO(
        @JsonProperty("certificate_type")
        val certificateType: String,
        @JsonProperty("certificate_type_code")
        val certificateTypeCode: String,
        @JsonProperty("certificate_usos_code")
        val certificateUsosCode: Char?,
        val comment: String?,
        @JsonProperty("document_number")
        val documentNumber: String?,
        @JsonProperty("document_year")
        val documentYear: Int?,
        @JsonProperty("issue_city")
        val issueCity: String?,
        @JsonProperty("issue_country")
        val issueCountry: String?,
        @JsonProperty("issue_date")
    val issueDate: Date?,
        @JsonProperty("issue_institution")
    val issueInstitution: String?,
        @JsonProperty("issue_institution_usos_code")
    val issueInstitutionUsosCode: String?,
        @JsonProperty("modification_date")
    val modificationDate: String?
)