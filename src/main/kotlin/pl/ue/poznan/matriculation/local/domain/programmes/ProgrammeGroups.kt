package pl.ue.poznan.matriculation.local.domain.programmes

import pl.ue.poznan.matriculation.local.domain.Name
import javax.persistence.*

@Entity
data class ProgrammeGroups(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        val code: String?,
        @OneToOne
        val name: Name?,
        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn(name = "programme_id",referencedColumnName = "id")
        val programmes: List<Programme?>?
)