package pl.poznan.ue.matriculation.irk.dto.registrations


import com.fasterxml.jackson.annotation.JsonProperty
import pl.poznan.ue.matriculation.local.domain.Name
import java.time.LocalDate

data class CycleDTO(
    val code: String?,
    @JsonProperty("date_from")
    val dateFrom: LocalDate?,
    @JsonProperty("date_to")
    val dateTo: LocalDate?,
    val name: Name?
)
