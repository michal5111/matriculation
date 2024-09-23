package pl.poznan.ue.matriculation.local.domain.registrations


import jakarta.persistence.Id
import pl.poznan.ue.matriculation.irk.dto.TurnDTO
import pl.poznan.ue.matriculation.local.domain.Name

open class Registration(

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
