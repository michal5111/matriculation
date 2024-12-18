package pl.poznan.ue.matriculation.local.domain.applicants

import jakarta.persistence.*
import pl.poznan.ue.matriculation.local.domain.BaseEntityApplicantId
import java.io.Serializable
import java.time.LocalDate

@Entity
@Table(name = "ApplicantForeignerData")
class ApplicantForeignerData(

    var baseOfStay: String?,

    @OneToMany(mappedBy = "applicantForeignerData", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var foreignerStatus: MutableSet<Status> = HashSet(),

    var polishCardIssueCountry: String?,

    @Temporal(TemporalType.DATE)
    var polishCardIssueDate: LocalDate?,

    var polishCardNumber: String?,

    @Temporal(TemporalType.DATE)
    var polishCardValidTo: LocalDate?,

    applicant: Applicant? = null
) : BaseEntityApplicantId(applicant), Serializable {

    override fun toString(): String {
        return "ApplicantForeignerData(baseOfStay=$baseOfStay, polishCardIssueCountry=$polishCardIssueCountry, " +
            "polishCardIssueDate=$polishCardIssueDate, polishCardNumber=$polishCardNumber, " +
            "polishCardValidTo=$polishCardValidTo)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApplicantForeignerData

        if (applicant != other.applicant) return false

        return true
    }

    override fun hashCode(): Int {
        return applicant?.hashCode() ?: 0
    }


}
