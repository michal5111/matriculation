package pl.ue.poznan.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.ue.poznan.matriculation.oracle.domain.Address
import pl.ue.poznan.matriculation.oracle.domain.AddressType
import pl.ue.poznan.matriculation.oracle.domain.Person
import pl.ue.poznan.matriculation.oracle.repo.CitizenshipRepository
import pl.ue.poznan.matriculation.oracle.repo.PostalCodeRepository

@Service
class AddressService(
        private val postalCodeRepository: PostalCodeRepository,
        private val citizenshipRepository: CitizenshipRepository
) {

    fun createAddress(
            person: Person? = null,
            addressType: AddressType,
            city: String?,
            street: String?,
            houseNumber: String?,
            apartmentNumber: String?,
            zipCode: String?,
            cityIsCity: Boolean,
            countryCode: String?
    ): Address {
        return Address(
                person = person,
                addressType = addressType,
                city = city,
                street = street,
                houseNumber = houseNumber,
                apartmentNumber = apartmentNumber,
                zipCode = zipCode,
                cityIsCity = if (cityIsCity) 'T' else 'N',
                countryCode = countryCode?.let {
                    citizenshipRepository.getOne(it)
                }
        ).getPostalCodeInfo()
    }

    fun updateAddress(
            address: Address,
            city: String?,
            street: String?,
            houseNumber: String?,
            apartmentNumber: String?,
            zipCode: String?,
            cityIsCity: Boolean,
            countryCode: String?
    ) {
        address.apply {
            this.city = city
            this.street = street
            this.houseNumber = houseNumber
            this.apartmentNumber = apartmentNumber
            this.zipCode = zipCode
            this.cityIsCity = if (cityIsCity) 'T' else 'N'
            this.countryCode = countryCode?.let {
                citizenshipRepository.getOne(it)
            }
        }.getPostalCodeInfo()
    }

    private fun Address.getPostalCodeInfo(): Address {
        zipCode.let {
            if (it != null && city != null) {
                val postalCodes = postalCodeRepository.findByCodeAndPostLike(zipCode!!, "$city")
                if (postalCodes.isEmpty()) {
                    return@let
                }
                commune = postalCodes[0].commune
                county = postalCodes[0].county
                if (cityIsCity == null) {
                    cityIsCity = postalCodes[0].cityIsCity
                }
            }
        }
        return this
    }
}