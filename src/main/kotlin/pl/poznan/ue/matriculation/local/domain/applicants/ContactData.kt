package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class ContactData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        var modificationDate: String?,

        var officialCity: String?,

        var officialCityIsCity: Boolean,

        var officialCountry: String?,

        var officialFlatNumber: String?,

        var officialPostCode: String?,

        var officialStreet: String?,

        var officialStreetNumber: String,

        var phoneNumber: String?,

        var phoneNumber2: String?,

        var phoneNumber2Type: String?,

        var phoneNumberType: String?,

        var realCity: String?,

        var realCityIsCity: Boolean,

        var realCountry: String?,

        var realFlatNumber: String?,

        var realPostCode: String?,

        var realStreet: String?,

        var realStreetNumber: String?
): Serializable {

    override fun toString(): String {
        return "ContactData(modificationDate=$modificationDate, officialCity=$officialCity, " +
                "officialCityIsCity=$officialCityIsCity, officialCountry=$officialCountry, " +
                "officialFlatNumber=$officialFlatNumber, officialPostCode=$officialPostCode, " +
                "officialStreet=$officialStreet, officialStreetNumber='$officialStreetNumber', " +
                "phoneNumber=$phoneNumber, phoneNumber2=$phoneNumber2, phoneNumber2Type=$phoneNumber2Type, " +
                "phoneNumberType=$phoneNumberType, realCity=$realCity, realCityIsCity=$realCityIsCity, " +
                "realCountry=$realCountry, realFlatNumber=$realFlatNumber, realPostCode=$realPostCode, " +
                "realStreet=$realStreet, realStreetNumber=$realStreetNumber)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactData

        if (applicant != other.applicant) return false

        return true
        }

        override fun hashCode(): Int {
            return applicant?.hashCode() ?: 0
        }


}