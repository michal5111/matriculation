package pl.ue.poznan.matriculation.local.domain.applications


import pl.ue.poznan.matriculation.irk.domain.applications.Application
import pl.ue.poznan.matriculation.local.domain.Turn
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant
import javax.persistence.*

@Entity
data class Application(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val admitted: String?,
        val comment: String?,
        @OneToOne
        val foreignerData: ForeignerData?,
        val payment: String?,
        val position: String?,
        val qualified: String?,
        val score: String?,
        @OneToOne
        val turn: Turn?,
        @OneToOne(fetch = FetchType.LAZY)
        val user: Applicant?
) {
        constructor(application: Application): this(
                admitted = application.admitted,
                comment = application.comment,
                foreignerData = ForeignerData(application.foreignerData!!),
                payment = application.payment,
                position = application.position,
                qualified = application.qualified,
                score = application.score,
                turn = Turn(application.turn!!),
                user = Applicant(application.user!!)
        )
}