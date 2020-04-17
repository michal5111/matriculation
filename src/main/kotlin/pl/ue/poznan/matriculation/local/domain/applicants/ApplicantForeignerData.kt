package pl.ue.poznan.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "ApplicantForeignerData")
data class ApplicantForeignerData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        var baseOfStay: String?,

        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "applicantForeignerData", cascade = [CascadeType.ALL])
        var foreignerStatus: List<Status>? = listOf(),

        var polishCardIssueCountry: String?,

        var polishCardIssueDate: String?,

        var polishCardNumber: String?,

        var polishCardValidTo: String?
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ApplicantForeignerData

                if (baseOfStay != other.baseOfStay) return false
                if (polishCardIssueCountry != other.polishCardIssueCountry) return false
                if (polishCardIssueDate != other.polishCardIssueDate) return false
                if (polishCardNumber != other.polishCardNumber) return false
                if (polishCardValidTo != other.polishCardValidTo) return false

                return true
        }

        override fun hashCode(): Int {
                var result = baseOfStay?.hashCode() ?: 0
                result = 31 * result + (polishCardIssueCountry?.hashCode() ?: 0)
                result = 31 * result + (polishCardIssueDate?.hashCode() ?: 0)
                result = 31 * result + (polishCardNumber?.hashCode() ?: 0)
                result = 31 * result + (polishCardValidTo?.hashCode() ?: 0)
                return result
        }

        override fun toString(): String {
                return "ApplicantForeignerData(baseOfStay=$baseOfStay, polishCardIssueCountry=$polishCardIssueCountry, polishCardIssueDate=$polishCardIssueDate, polishCardNumber=$polishCardNumber, polishCardValidTo=$polishCardValidTo)"
        }


}