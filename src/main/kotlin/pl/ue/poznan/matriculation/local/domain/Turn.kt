package pl.ue.poznan.matriculation.local.domain


import pl.ue.poznan.matriculation.irk.domain.Turn
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
        constructor(turn: Turn): this(
                dateFrom = turn.dateFrom,
                dateTo = turn.dateTo,
                programme = turn.programme,
                registration = turn.registration
        )
}