package pl.poznan.ue.matriculation.local.domain.applicants


import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
class AdditionalData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        var cityOfBirth: String?,

        var countryOfBirth: String?,

        var documentCountry: String?,

        var documentExpDate: Date?,

        var documentNumber: String?,

        var documentType: Char?,

        var fathersName: String?,

        var militaryCategory: String?,

        var militaryStatus: String?,

        var mothersName: String?,

        var wku: String?
) : Serializable {

    override fun toString(): String {
        return "AdditionalData(cityOfBirth=$cityOfBirth, countryOfBirth=$countryOfBirth, " +
                "documentCountry=$documentCountry, documentExpDate=$documentExpDate, " +
                "documentNumber=$documentNumber, documentType=$documentType, " +
                "fathersName=$fathersName, militaryCategory=$militaryCategory, " +
                "militaryStatus=$militaryStatus, mothersName=$mothersName, wku=$wku)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdditionalData

        if (applicant != other.applicant) return false

        return true
    }

    override fun hashCode(): Int {
        return applicant?.hashCode() ?: 0
    }


}