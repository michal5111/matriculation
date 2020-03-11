package pl.ue.poznan.matriculation.local.domain.applications


import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
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
//        constructor(applicationDTO: ApplicationDTO): this(
//                admitted = applicationDTO.admitted,
//                comment = applicationDTO.comment,
//                foreignerData = ForeignerData(applicationDTO.foreignerDataDTO!!),
//                payment = applicationDTO.payment,
//                position = applicationDTO.position,
//                qualified = applicationDTO.qualified,
//                score = applicationDTO.score,
//                turn = Turn(applicationDTO.turn!!),
//                user = Applicant(applicationDTO.user!!)
//        )
}