package pl.ue.poznan.matriculation.irk.dto.registrations


import com.fasterxml.jackson.annotation.JsonProperty
import pl.ue.poznan.matriculation.irk.dto.TurnDTO
import pl.ue.poznan.matriculation.local.domain.Name
import pl.ue.poznan.matriculation.local.domain.registrations.Cycle

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