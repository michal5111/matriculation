package pl.ue.poznan.matriculation.local.domain.registrations


import pl.ue.poznan.matriculation.local.domain.Name
import pl.ue.poznan.matriculation.local.domain.Turn
import pl.ue.poznan.matriculation.local.domain.programmes.Programme
import javax.persistence.*

@Entity
data class Registration(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        val code: String?,
        @OneToOne
        val cycle: Cycle?,
        val employeesOnly: Boolean?,
        @OneToOne
        val name: Name?,
        @OneToMany
        val programmes: List<Programme?>?,
        val status: String?,
        val tag: String?,
        @OneToMany
        val turns: List<Turn?>?
)