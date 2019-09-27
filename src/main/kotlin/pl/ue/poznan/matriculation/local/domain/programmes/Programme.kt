package pl.ue.poznan.matriculation.local.domain.programmes

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Programme(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: String

//        @OneToMany(mappedBy = "programmeGroups")
//        val programmeGroup: ProgrammeGroups
)