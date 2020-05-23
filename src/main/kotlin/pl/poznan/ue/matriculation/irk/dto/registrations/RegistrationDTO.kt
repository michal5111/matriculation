package pl.poznan.ue.matriculation.irk.dto.registrations


import com.fasterxml.jackson.annotation.JsonProperty
import pl.poznan.ue.matriculation.irk.dto.TurnDTO
import pl.poznan.ue.matriculation.local.domain.Name
import pl.poznan.ue.matriculation.local.domain.registrations.Cycle

data class RegistrationDTO(
        val code: String,
        val cycle: Cycle,
        @JsonProperty("employees_only")
        val employeesOnly: Boolean?,
        val name: Name,
        val programmes: List<String?>,
        val status: String,
        val tag: String?,
        val turn: List<TurnDTO?>?
)