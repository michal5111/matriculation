package pl.poznan.ue.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.oracle.domain.Address
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.repo.AddressTypeRepository
import pl.poznan.ue.matriculation.oracle.repo.CitizenshipRepository
import pl.poznan.ue.matriculation.oracle.repo.PostalCodeRepository
import java.time.LocalDate

@Service
class AddressService(
    private val postalCodeRepository: PostalCodeRepository,
    private val citizenshipRepository: CitizenshipRepository,
    private val addressTypeRepository: AddressTypeRepository
) {

    fun create(
        person: Person? = null,
        addressTypeCode: String,
        city: String?,
        street: String?,
        houseNumber: String?,
        apartmentNumber: String?,
        zipCode: String?,
        cityIsCity: Boolean?,
        countryCode: String?,
        dateFrom: LocalDate? = null
    ): Address {
        return Address(
            person = person,
            addressType = addressTypeRepository.getReferenceById(addressTypeCode),
            city = city,
            street = street,
            houseNumber = houseNumber,
            flatNumber = apartmentNumber,
            cityIsCity = cityIsCity,
            country = countryCode?.let {
                citizenshipRepository.getReferenceById(it)
            },
            dateFrom = dateFrom
        ).apply {
            countryCode?.let {
                if (it == "PL" && zipCode?.length == 5) {
                    this.zipCode = zipCode
                } else {
                    this.foreignZipCode = zipCode
                }
            }
        }.getPostalCodeInfo()
    }

    fun update(
        address: Address,
        city: String?,
        street: String?,
        houseNumber: String?,
        apartmentNumber: String?,
        zipCode: String?,
        cityIsCity: Boolean?,
        countryCode: String?
    ) {
        address.apply {
            this.city = city
            this.street = street
            this.houseNumber = houseNumber
            this.flatNumber = apartmentNumber
            this.cityIsCity = cityIsCity
            this.country = countryCode?.let {
                citizenshipRepository.getReferenceById(it)
            }
            countryCode?.let {
                if (it == "PL" && zipCode?.length == 5) {
                    this.foreignZipCode = null
                    this.zipCode = zipCode
                } else {
                    this.zipCode = null
                    this.foreignZipCode = zipCode
                }
            }
        }.getPostalCodeInfo()
    }

    private fun Address.getPostalCodeInfo(): Address {
        zipCode?.let {
            if (city != null) {
                val postalCodes = postalCodeRepository.findByCodeAndPostLike(zipCode, "$city%")
                if (postalCodes.isEmpty()) {
                    return@let
                }
                commune = postalCodes.first().commune
                county = postalCodes.first().county
                cityIsCity = postalCodes.first().cityIsCity
            }
        }
        return this
    }
}
