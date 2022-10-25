package pl.poznan.ue.matriculation.local.processor

import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.service.AddressService

class AddressesProcessor(
    private val addressService: AddressService,
    private val targetSystemProcessor: TargetSystemProcessor<Person?>
) : TargetSystemProcessor<Person?> {
    override fun process(processRequest: ProcessRequest): ProcessResult<Person?> {
        val person = processRequest.person!!
        val applicant = processRequest.application.applicant!!
        applicant.addresses.forEach {
            createOrUpdateAddress(
                person = person,
                addressTypeCode = it.addressType.usosValue,
                city = it.city,
                street = it.street,
                houseNumber = it.streetNumber,
                apartmentNumber = it.flatNumber,
                zipCode = it.postalCode,
                cityIsCity = it.cityIsCity,
                countryCode = it.countryCode
            )
        }
        if (person.addresses.none { it.addressType.code == "KOR" }) {
            val foundAddress = person.addresses.find { it.addressType.code == "POB" }
                ?: person.addresses.find { it.addressType.code == "STA" }
            foundAddress?.let {
                createOrUpdateAddress(
                    person = person,
                    addressTypeCode = "KOR",
                    city = it.city,
                    street = it.street,
                    houseNumber = it.houseNumber,
                    apartmentNumber = it.flatNumber,
                    zipCode = it.zipCode,
                    cityIsCity = it.cityIsCity,
                    countryCode = it.country?.code
                )
            }
        }
        return targetSystemProcessor.process(processRequest)
    }

    private fun createOrUpdateAddress(
        person: Person,
        addressTypeCode: String,
        city: String?,
        street: String?,
        houseNumber: String?,
        apartmentNumber: String?,
        zipCode: String?,
        cityIsCity: Boolean?,
        countryCode: String?
    ) {
        val address = person.addresses.find { it.addressType.code == addressTypeCode }
        if (address != null) {
            addressService.update(
                address = address,
                city = city,
                street = street,
                houseNumber = houseNumber,
                apartmentNumber = apartmentNumber,
                zipCode = zipCode,
                cityIsCity = cityIsCity,
                countryCode = countryCode
            )
        } else {
            person.addAddress(
                addressService.create(
                    person = person,
                    addressTypeCode = addressTypeCode,
                    city = city,
                    street = street,
                    houseNumber = houseNumber,
                    apartmentNumber = apartmentNumber,
                    zipCode = zipCode,
                    cityIsCity = cityIsCity,
                    countryCode = countryCode
                )
            )
        }
    }
}
