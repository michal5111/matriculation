package pl.poznan.ue.matriculation.local.domain.applicants

import jakarta.persistence.*
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import pl.poznan.ue.matriculation.local.domain.enum.AddressType
import java.io.Serializable

@Entity
class Address(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    var applicant: Applicant? = null,

    @Enumerated(EnumType.STRING)
    var addressType: AddressType,

    var city: String?,

    var cityIsCity: Boolean,

    var countryCode: String?,

    var flatNumber: String?,

    var postalCode: String?,

    var street: String?,

    var streetNumber: String?
) : BaseEntityLongId(), Serializable {


    override fun toString(): String {
        return "Address(id=$id, applicant=$applicant, addressType=$addressType, city=$city, cityIsCity=$cityIsCity, country=$countryCode, flatNumber=$flatNumber, postalCode=$postalCode, street=$street, streetNumber=$streetNumber)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Address) return false

        if (addressType != other.addressType) return false
        if (city != other.city) return false
        if (cityIsCity != other.cityIsCity) return false
        if (countryCode != other.countryCode) return false
        if (flatNumber != other.flatNumber) return false
        if (postalCode != other.postalCode) return false
        if (street != other.street) return false
        if (streetNumber != other.streetNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addressType.hashCode()
        result = 31 * result + (city?.hashCode() ?: 0)
        result = 31 * result + cityIsCity.hashCode()
        result = 31 * result + (countryCode?.hashCode() ?: 0)
        result = 31 * result + (flatNumber?.hashCode() ?: 0)
        result = 31 * result + (postalCode?.hashCode() ?: 0)
        result = 31 * result + (street?.hashCode() ?: 0)
        result = 31 * result + (streetNumber?.hashCode() ?: 0)
        return result
    }


}
