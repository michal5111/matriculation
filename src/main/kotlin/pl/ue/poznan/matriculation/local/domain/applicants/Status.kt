package pl.ue.poznan.matriculation.local.domain.applicants

import pl.ue.poznan.matriculation.irk.dto.applicants.StatusDTO
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Status(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val status: String
) {
    constructor(statusDTO: StatusDTO): this(
            status = statusDTO.status
    )
}