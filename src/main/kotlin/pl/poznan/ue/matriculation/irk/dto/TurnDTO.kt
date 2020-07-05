package pl.poznan.ue.matriculation.irk.dto


import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TurnDTO(
        @JsonProperty("date_from")
        val dateFrom: Date?,
        @JsonProperty("date_to")
        val dateTo: Date?,
        val programme: String?,
        val registration: String?
)