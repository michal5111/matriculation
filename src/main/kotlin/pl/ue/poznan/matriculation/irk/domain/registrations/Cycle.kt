package pl.ue.poznan.matriculation.irk.domain.registrations


import com.fasterxml.jackson.annotation.JsonProperty
import pl.ue.poznan.matriculation.local.domain.Name
import java.util.*

data class Cycle(
    val code: String?,
    @JsonProperty("date_from")
    val dateFrom: Date?,
    @JsonProperty("date_to")
    val dateTo: Date?,
    val name: Name?
)