package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ContactData(

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
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ContactData

                if (modificationDate != other.modificationDate) return false
                if (officialCity != other.officialCity) return false
                if (officialCityIsCity != other.officialCityIsCity) return false
                if (officialCountry != other.officialCountry) return false
                if (officialFlatNumber != other.officialFlatNumber) return false
                if (officialPostCode != other.officialPostCode) return false
                if (officialStreet != other.officialStreet) return false
                if (officialStreetNumber != other.officialStreetNumber) return false
                if (phoneNumber != other.phoneNumber) return false
                if (phoneNumber2 != other.phoneNumber2) return false
                if (phoneNumber2Type != other.phoneNumber2Type) return false
                if (phoneNumberType != other.phoneNumberType) return false
                if (realCity != other.realCity) return false
                if (realCityIsCity != other.realCityIsCity) return false
                if (realCountry != other.realCountry) return false
                if (realFlatNumber != other.realFlatNumber) return false
                if (realPostCode != other.realPostCode) return false
                if (realStreet != other.realStreet) return false
                if (realStreetNumber != other.realStreetNumber) return false

                return true
        }

        override fun hashCode(): Int {
                var result = modificationDate?.hashCode() ?: 0
                result = 31 * result + (officialCity?.hashCode() ?: 0)
                result = 31 * result + officialCityIsCity.hashCode()
                result = 31 * result + (officialCountry?.hashCode() ?: 0)
                result = 31 * result + (officialFlatNumber?.hashCode() ?: 0)
                result = 31 * result + (officialPostCode?.hashCode() ?: 0)
                result = 31 * result + (officialStreet?.hashCode() ?: 0)
                result = 31 * result + officialStreetNumber.hashCode()
                result = 31 * result + (phoneNumber?.hashCode() ?: 0)
                result = 31 * result + (phoneNumber2?.hashCode() ?: 0)
                result = 31 * result + (phoneNumber2Type?.hashCode() ?: 0)
                result = 31 * result + (phoneNumberType?.hashCode() ?: 0)
                result = 31 * result + (realCity?.hashCode() ?: 0)
                result = 31 * result + realCityIsCity.hashCode()
                result = 31 * result + (realCountry?.hashCode() ?: 0)
                result = 31 * result + (realFlatNumber?.hashCode() ?: 0)
                result = 31 * result + (realPostCode?.hashCode() ?: 0)
                result = 31 * result + (realStreet?.hashCode() ?: 0)
                result = 31 * result + (realStreetNumber?.hashCode() ?: 0)
                return result
        }

        override fun toString(): String {
                return "ContactData(modificationDate=$modificationDate, officialCity=$officialCity, officialCityIsCity=$officialCityIsCity, officialCountry=$officialCountry, officialFlatNumber=$officialFlatNumber, officialPostCode=$officialPostCode, officialStreet=$officialStreet, officialStreetNumber='$officialStreetNumber', phoneNumber=$phoneNumber, phoneNumber2=$phoneNumber2, phoneNumber2Type=$phoneNumber2Type, phoneNumberType=$phoneNumberType, realCity=$realCity, realCityIsCity=$realCityIsCity, realCountry=$realCountry, realFlatNumber=$realFlatNumber, realPostCode=$realPostCode, realStreet=$realStreet, realStreetNumber=$realStreetNumber)"
        }


}