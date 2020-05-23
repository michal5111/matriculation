package pl.poznan.ue.matriculation.irk.dto.registrations


import com.fasterxml.jackson.annotation.JsonProperty
import pl.poznan.ue.matriculation.local.domain.Name
import java.util.*

data class CycleDTO(
    val code: String?,
    @JsonProperty("date_from")
    val dateFrom: Date?,
    @JsonProperty("date_to")
    val dateTo: Date?,
    val name: Name?
)