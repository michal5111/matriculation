package pl.poznan.ue.matriculation.local.domain.registrations


import jakarta.persistence.Id
import pl.poznan.ue.matriculation.local.domain.Name
import java.util.*

open class Cycle(

    @Id
    val code: String,

    val dateFrom: Date?,

    val dateTo: Date?,

    val name: Name?
)
