package pl.ue.poznan.matriculation.irk.domain.applicants

import com.fasterxml.jackson.annotation.JsonProperty
import pl.ue.poznan.matriculation.irk.dto.applicants.*
import java.util.*

data class Applicant(
        val id: Long,

        val email: String,

        @JsonProperty("index_number")
        val indexNumber: String?,

        val password: String,

        val nameDTO: NameDTO,

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
        val basicDataDTO: BasicDataDTO,

        @JsonProperty("contact_data")
        val contactDataDTO: ContactDataDTO?,

        @JsonProperty("additional_data")
        val additionalDataDTO: AdditionalDataDTO?,

        @JsonProperty("foreigner_data")
        val foreignerDataDTO: ForeignerDataDTO?,

        @JsonProperty("education_data")
        val educationDataDTO: EducationDataDTO?
)