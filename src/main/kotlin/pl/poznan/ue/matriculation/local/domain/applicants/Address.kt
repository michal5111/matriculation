package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.enum.AddressType
import java.io.Serializable
import javax.persistence.*

@Entity
class Address(
        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        val applicant: Applicant? = null,

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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Address(id=$id, applicant=$applicant, addressType=$addressType, city=$city, cityIsCity=$cityIsCity, country=$countryCode, flatNumber=$flatNumber, postalCode=$postalCode, street=$street, streetNumber=$streetNumber)"
    }


}