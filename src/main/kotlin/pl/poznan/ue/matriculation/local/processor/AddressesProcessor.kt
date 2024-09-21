package pl.poznan.ue.matriculation.local.processor

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.service.AddressService
import java.util.*

open class AddressesProcessor(
    private val addressService: AddressService,
    targetSystemProcessor: TargetSystemProcessor<Person>
) : ProcessDecorator<Person>(targetSystemProcessor) {
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    override fun process(processRequest: ProcessRequest): ProcessResult<Person> {
        return super.process(processRequest).also { processResult ->
            val person = processResult.person
            val applicant = processRequest.application.applicant ?: return@also
            applicant.addresses.forEach {
                createAddress(
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
            if (applicant.addresses.none { it.addressType.usosValue == "KOR" }) {
                val foundAddress = person.addresses.find { it.addressType.code == "POB" }
                    ?: person.addresses.find { it.addressType.code == "STA" }
                foundAddress?.let {
                    createAddress(
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
        }
    }

    private fun createAddress(
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
        val existingAddress = person.addresses.filter {
            val date = it.dateTo
            date == null || date <= Date()
        }.find { it.addressType.code == addressTypeCode }
        if (
            existingAddress?.city == city
            && existingAddress?.country?.code == countryCode
            && existingAddress?.flatNumber == apartmentNumber
            && existingAddress?.houseNumber == houseNumber
            && existingAddress?.street == street
            && (countryCode == "PL" && existingAddress?.zipCode == zipCode) || (countryCode != "PL" && existingAddress?.foreignZipCode == zipCode)
        ) {
            return
        } else {
            existingAddress?.dateTo = Date()
        }
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
                countryCode = countryCode,
                dateFrom = existingAddress?.dateTo
            )
        )
    }
}
