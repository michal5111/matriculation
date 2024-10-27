package pl.poznan.ue.matriculation.local.domain.registrations


import jakarta.persistence.Id
import pl.poznan.ue.matriculation.local.domain.Name
import java.time.LocalDate

open class Cycle(

    @Id
    val code: String,

    val dateFrom: LocalDate?,

    val dateTo: LocalDate?,

    val name: Name?
)
