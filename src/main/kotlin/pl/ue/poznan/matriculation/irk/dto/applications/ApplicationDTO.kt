package pl.ue.poznan.matriculation.irk.dto.applications


import com.fasterxml.jackson.annotation.JsonProperty
import pl.ue.poznan.matriculation.irk.dto.TurnDTO

data class ApplicationDTO(
        val admitted: String?,
        val comment: String?,
        @JsonProperty("foreigner_data")
    val foreignerDataDTO: ForeignerDataDTO?,
        val id: Long,
        val payment: String?,
        val position: String?,
        val qualified: String?,
        val score: String?,
        val turn: TurnDTO?,
        val user: Long
)