package pl.poznan.ue.matriculation.irk.dto.applications


import com.fasterxml.jackson.annotation.JsonProperty
import pl.poznan.ue.matriculation.irk.dto.TurnDTO
import pl.poznan.ue.matriculation.irk.dto.applicants.DocumentDTO
import pl.poznan.ue.matriculation.local.dto.AbstractApplicationDto
import java.io.Serializable

data class ApplicationDTO(
        val admitted: String?,
        val comment: String?,
        @JsonProperty("foreigner_data")
        val foreignerData: ForeignerDataDTO?,
        val id: Long,
        val payment: String?,
        val position: String?,
        val qualified: String?,
        val score: String?,
        val turn: TurnDTO,
        val user: Long,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        var certificate: DocumentDTO? = null
) : Serializable, AbstractApplicationDto() {
        override fun getForeignApplicantId(): Long {
                return user
        }

        override fun getForeignId(): Long {
                return id
        }
}