package pl.ue.poznan.matriculation.local.domain.applicants

import pl.ue.poznan.matriculation.irk.dto.applicants.NameDTO
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
        constructor(nameDTO: NameDTO): this(
                middle = nameDTO.middle,
                family = nameDTO.family,
                given = nameDTO.given,
                maiden = nameDTO.maiden
        )
}