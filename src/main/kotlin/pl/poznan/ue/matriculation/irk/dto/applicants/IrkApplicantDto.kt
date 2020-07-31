package pl.poznan.ue.matriculation.irk.dto.applicants

import com.fasterxml.jackson.annotation.JsonProperty
import pl.poznan.ue.matriculation.local.dto.AbstractApplicantDto
import java.io.Serializable
import java.util.*

data class IrkApplicantDto(
        val id: Long,

        val email: String,

        @JsonProperty("index_number")
        val indexNumber: String?,

        val password: String,

        val name: NameDTO,

        val phone: String?,

        val citizenship: String,

        val photo: String?,

        @JsonProperty("photo_permission")
        val photoPermission: String?,

        @JsonProperty("cas_password_override")
        val casPasswordOverride: String?,

        @JsonProperty("modification_date")
        val modificationDate: Date,

        @JsonProperty("basic_data")
        val basicData: BasicDataDTO,

        @JsonProperty("contact_data")
        val contactData: ContactDataDTO,

        @JsonProperty("additional_data")
        val additionalData: AdditionalDataDTO,

        @JsonProperty("foreigner_data")
        val foreignerData: ForeignerDataDTO?,

        @JsonProperty("education_data")
        val educationData: EducationDataDTO
) : Serializable, AbstractApplicantDto() {
    override fun getForeignId(): Long {
        return id
    }
}