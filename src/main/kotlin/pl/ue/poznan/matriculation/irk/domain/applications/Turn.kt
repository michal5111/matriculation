package pl.ue.poznan.matriculation.irk.domain.applications


import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Turn(
    @JsonProperty("date_from")
    val dateFrom: Date?,
    @JsonProperty("date_to")
    val dateTo: Date?,
    val programme: String?,
    val registration: String?
)