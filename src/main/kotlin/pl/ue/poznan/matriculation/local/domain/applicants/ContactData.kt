package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.domain.applicants.ContactData
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
        constructor(contactData: ContactData): this(
                modificationDate = contactData.modificationDate,
                officialCity = contactData.officialCity,
                officialCityIsCity = contactData.officialCityIsCity,
                officialCountry = contactData.officialCountry,
                officialFlatNumber = contactData.officialFlatNumber,
                officialPostCode = contactData.officialPostCode,
                officialStreet = contactData.officialStreet,
                officialStreetNumber = contactData.officialStreetNumber,
                phoneNumber = contactData.phoneNumber,
                phoneNumber2 = contactData.phoneNumber2,
                phoneNumber2Type = contactData.phoneNumber2Type,
                phoneNumberType = contactData.phoneNumber2Type,
                realCity = contactData.realCity,
                realCountry = contactData.realCountry,
                realCityIsCity = contactData.realCityIsCity,
                realFlatNumber = contactData.realFlatNumber,
                realPostCode = contactData.realPostCode,
                realStreet = contactData.realStreet,
                realStreetNumber = contactData.realStreetNumber
        )
}