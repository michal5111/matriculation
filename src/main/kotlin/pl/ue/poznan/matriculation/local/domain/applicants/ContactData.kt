package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.dto.applicants.ContactDataDTO
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class ContactData(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1,
        val modificationDate: String?,
        val officialCity: String?,
        val officialCityIsCity: Boolean,
        val officialCountry: String?,
        val officialFlatNumber: String?,
        val officialPostCode: String?,
        val officialStreet: String?,
        val officialStreetNumber: String,
        val phoneNumber: String?,
        val phoneNumber2: String?,
        val phoneNumber2Type: String?,
        val phoneNumberType: String?,
        val realCity: String?,
        val realCityIsCity: Boolean,
        val realCountry: String?,
        val realFlatNumber: String?,
        val realPostCode: String?,
        val realStreet: String?,
        val realStreetNumber: String?
) {
        constructor(contactDataDTO: ContactDataDTO): this(
                modificationDate = contactDataDTO.modificationDate,
                officialCity = contactDataDTO.officialCity,
                officialCityIsCity = contactDataDTO.officialCityIsCity,
                officialCountry = contactDataDTO.officialCountry,
                officialFlatNumber = contactDataDTO.officialFlatNumber,
                officialPostCode = contactDataDTO.officialPostCode,
                officialStreet = contactDataDTO.officialStreet,
                officialStreetNumber = contactDataDTO.officialStreetNumber,
                phoneNumber = contactDataDTO.phoneNumber,
                phoneNumber2 = contactDataDTO.phoneNumber2,
                phoneNumber2Type = contactDataDTO.phoneNumber2Type,
                phoneNumberType = contactDataDTO.phoneNumber2Type,
                realCity = contactDataDTO.realCity,
                realCountry = contactDataDTO.realCountry,
                realCityIsCity = contactDataDTO.realCityIsCity,
                realFlatNumber = contactDataDTO.realFlatNumber,
                realPostCode = contactDataDTO.realPostCode,
                realStreet = contactDataDTO.realStreet,
                realStreetNumber = contactDataDTO.realStreetNumber
        )
}