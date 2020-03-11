package pl.ue.poznan.matriculation.local.domain


import pl.ue.poznan.matriculation.irk.dto.TurnDTO
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Turn(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val dateFrom: Date?,
        val dateTo: Date?,
        val programme: String?,
        val registration: String?
) {
        constructor(turnDTO: TurnDTO): this(
                dateFrom = turnDTO.dateFrom,
                dateTo = turnDTO.dateTo,
                programme = turnDTO.programme,
                registration = turnDTO.registration
        )
}