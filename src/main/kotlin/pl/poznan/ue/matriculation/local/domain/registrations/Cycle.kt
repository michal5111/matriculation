package pl.poznan.ue.matriculation.local.domain.registrations


import pl.poznan.ue.matriculation.local.domain.Name
import java.util.*
import javax.persistence.Id

data class Cycle(

        @Id
        val code: String,

        val dateFrom: Date?,

        val dateTo: Date?,

        val name: Name?
)