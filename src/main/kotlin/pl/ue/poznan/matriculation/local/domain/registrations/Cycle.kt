package pl.ue.poznan.matriculation.local.domain.registrations


import pl.ue.poznan.matriculation.local.domain.Name
import java.util.*
import javax.persistence.*

@Entity
data class Cycle(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        val code: String?,
        val dateFrom: Date?,
        val dateTo: Date?,
        @OneToOne
        val name: Name?
)