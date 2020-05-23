package pl.poznan.ue.matriculation.irk.dto.applications


import com.fasterxml.jackson.annotation.JsonProperty
import pl.poznan.ue.matriculation.irk.dto.TurnDTO
import pl.poznan.ue.matriculation.local.domain.applicants.Document

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
        var irkInstance: String? = null,
        val certificate: Document?
)