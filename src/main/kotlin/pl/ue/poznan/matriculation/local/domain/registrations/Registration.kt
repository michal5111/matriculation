package pl.ue.poznan.matriculation.local.domain.registrations


import pl.ue.poznan.matriculation.irk.dto.TurnDTO
import pl.ue.poznan.matriculation.local.domain.Name
import javax.persistence.Id

data class Registration(

        @Id
        val code: String,
        val cycle: Cycle?,
        val employeesOnly: Boolean?,
        val name: Name?,
        val programmes: List<String?>?,
        val status: String?,
        val tag: String?,
        val turnDTOS: List<TurnDTO?>?
)