package pl.ue.poznan.matriculation.local.domain.applicants


import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
data class AdditionalData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        var cityOfBirth: String?,

        var countryOfBirth: String?,

        var documentCountry: String?,

        var documentExpDate: Date?,

        @Column(unique = true)
        var documentNumber: String?,

        var documentType: String?,

        var fathersName: String?,

        var militaryCategory: String?,

        var militaryStatus: String?,

        var mothersName: String?,

        var wku: String?
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as AdditionalData

                if (cityOfBirth != other.cityOfBirth) return false
                if (countryOfBirth != other.countryOfBirth) return false
                if (documentCountry != other.documentCountry) return false
                if (documentExpDate != other.documentExpDate) return false
                if (documentNumber != other.documentNumber) return false
                if (documentType != other.documentType) return false
                if (fathersName != other.fathersName) return false
                if (militaryCategory != other.militaryCategory) return false
                if (militaryStatus != other.militaryStatus) return false
                if (mothersName != other.mothersName) return false
                if (wku != other.wku) return false

                return true
        }

        override fun hashCode(): Int {
                var result = cityOfBirth?.hashCode() ?: 0
                result = 31 * result + (countryOfBirth?.hashCode() ?: 0)
                result = 31 * result + (documentCountry?.hashCode() ?: 0)
                result = 31 * result + (documentExpDate?.hashCode() ?: 0)
                result = 31 * result + (documentNumber?.hashCode() ?: 0)
                result = 31 * result + (documentType?.hashCode() ?: 0)
                result = 31 * result + (fathersName?.hashCode() ?: 0)
                result = 31 * result + (militaryCategory?.hashCode() ?: 0)
                result = 31 * result + (militaryStatus?.hashCode() ?: 0)
                result = 31 * result + (mothersName?.hashCode() ?: 0)
                result = 31 * result + (wku?.hashCode() ?: 0)
                return result
        }
}