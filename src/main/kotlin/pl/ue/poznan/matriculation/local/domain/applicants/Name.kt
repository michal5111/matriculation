package pl.ue.poznan.matriculation.local.domain.applicants

import pl.ue.poznan.matriculation.irk.domain.applicants.Name
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Name(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val middle: String?,
        val family: String?,
        val given: String?,
        val maiden: String?
) {
        constructor(name: Name): this(
                middle = name.middle,
                family = name.family,
                given = name.given,
                maiden = name.maiden
        )
}