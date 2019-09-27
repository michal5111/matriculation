package pl.ue.poznan.matriculation.irk.domain.registrations


import com.fasterxml.jackson.annotation.JsonProperty
import pl.ue.poznan.matriculation.local.domain.Name
import pl.ue.poznan.matriculation.irk.domain.Turn
import pl.ue.poznan.matriculation.local.domain.registrations.Cycle

data class Registration(
        val code: String?,
        val cycle: Cycle?,
        @JsonProperty("employees_only")
        val employeesOnly: Boolean?,
        val name: Name?,
        val programmes: List<String?>?,
        val status: String?,
        val tag: String?,
        val turns: List<Turn?>?
)