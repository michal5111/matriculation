package pl.poznan.ue.matriculation.irk.dto


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class TurnDTO(
    @JsonProperty("date_from")
    val dateFrom: ZonedDateTime?,
    @JsonProperty("date_to")
    val dateTo: ZonedDateTime?,
    val programme: String?,
    val registration: String?
)
