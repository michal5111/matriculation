package pl.ue.poznan.matriculation.irk.domain.applicants

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Applicant(
        val id: Long,

        val email: String,

        @JsonProperty("index_number")
        val indexNumber: String?,

        val password: String,

        val name: Name,

        val phone: String?,

        val citizenship: String?,

        val photo: String?,

        @JsonProperty("photo_permission")
        val photoPermission: String?,

        @JsonProperty("cas_password_override")
        val casPasswordOverride: String?,

        @JsonProperty("modification_date")
        val modificationDate: Date,

        @JsonProperty("basic_data")
        val basicData: BasicData,

        @JsonProperty("contact_data")
        val contactData: ContactData?,

        @JsonProperty("additional_data")
        val additionalData: AdditionalData?,

        @JsonProperty("foreigner_data")
        val foreignerData: ForeignerData?,

        @JsonProperty("education_data")
        val educationData: EducationData?
)