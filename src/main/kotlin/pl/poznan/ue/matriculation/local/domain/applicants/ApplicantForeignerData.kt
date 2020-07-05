package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ApplicantForeignerData")
class ApplicantForeignerData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        var baseOfStay: String?,

        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "applicantForeignerData", cascade = [CascadeType.ALL])
        var foreignerStatus: MutableList<Status> = mutableListOf(),

        var polishCardIssueCountry: String?,

        var polishCardIssueDate: Date?,

        var polishCardNumber: String?,

        var polishCardValidTo: Date?
) : Serializable {

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