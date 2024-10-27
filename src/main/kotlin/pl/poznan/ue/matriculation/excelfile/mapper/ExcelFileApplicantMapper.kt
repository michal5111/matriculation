package pl.poznan.ue.matriculation.excelfile.mapper

import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicantDto
import pl.poznan.ue.matriculation.kotlinExtensions.trimPhoneNumber
import pl.poznan.ue.matriculation.kotlinExtensions.trimPostalCode
import pl.poznan.ue.matriculation.local.domain.applicants.Address
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument
import pl.poznan.ue.matriculation.local.domain.applicants.PhoneNumber
import pl.poznan.ue.matriculation.local.domain.enum.AddressType
import java.time.LocalDate
import java.time.ZonedDateTime

class ExcelFileApplicantMapper {

    fun mapExcelFileApplicantToApplicant(excelFileApplicant: ExcelFileApplicantDto): Applicant {
        return Applicant(
            foreignId = excelFileApplicant.id,
            given = excelFileApplicant.given,
            middle = excelFileApplicant.middle,
            family = excelFileApplicant.family,
            maiden = null,
            email = excelFileApplicant.email,
            cityOfBirth = excelFileApplicant.birthPlace,
            countryOfBirth = null,
            dateOfBirth = excelFileApplicant.birthDate,
            pesel = excelFileApplicant.pesel,
            sex = excelFileApplicant.sex,
            fathersName = excelFileApplicant.fathersName,
            mothersName = excelFileApplicant.mothersName,
            militaryCategory = null,
            militaryStatus = null,
            wku = null,
            citizenship = excelFileApplicant.citizenship,
            applicantForeignerData = null,
            indexNumber = null,
            modificationDate = ZonedDateTime.now(),
            password = null,
            photo = null,
            photoPermission = null
        ).apply {
            excelFileApplicant.phoneNumber?.let {
                phoneNumbers += PhoneNumber(
                    applicant = this,
                    comment = "Podstawowy numer telefonu",
                    phoneNumberType = "KOM",
                    number = it
                )
            }
            excelFileApplicant.passport?.let {
                identityDocuments.add(
                    IdentityDocument(
                        applicant = this,
                        type = "P",
                        number = it.replace("[^a-zA-Z0-9]+", ""),
                        country = "PL",
                        expDate = LocalDate.now()
                    )
                )
            }
            addresses.add(
                Address(
                    applicant = this,
                    addressType = AddressType.PERMANENT,
                    city = excelFileApplicant.address.city,
                    cityIsCity = false,
                    countryCode = excelFileApplicant.address.countryCode,
                    street = excelFileApplicant.address.street,
                    streetNumber = excelFileApplicant.address.streetNumber,
                    flatNumber = excelFileApplicant.address.flatNumber,
                    postalCode = excelFileApplicant.address.postalCode?.trimPostalCode()
                )
            )
        }
    }

    fun updateApplicantFromExcelApplicantDto(
        applicant: Applicant,
        excelFileApplicant: ExcelFileApplicantDto
    ): Applicant {
        return applicant.apply {
            given = excelFileApplicant.given
            middle = excelFileApplicant.middle
            family = excelFileApplicant.family
            email = excelFileApplicant.email.trim()
            cityOfBirth = excelFileApplicant.birthPlace.trim()
            countryOfBirth = null
            dateOfBirth = excelFileApplicant.birthDate
            pesel = excelFileApplicant.pesel?.trim()
            sex = excelFileApplicant.sex
            fathersName = excelFileApplicant.fathersName
            mothersName = excelFileApplicant.mothersName
            citizenship = excelFileApplicant.citizenship.trim()
            modificationDate = ZonedDateTime.now()
            excelFileApplicant.phoneNumber?.trimPhoneNumber()?.let {
                phoneNumbers += PhoneNumber(
                    comment = "Podstawowy numer telefonu",
                    phoneNumberType = "KOM",
                    number = it
                )
            }
            excelFileApplicant.passport?.let {
                identityDocuments.add(
                    IdentityDocument(
                        type = "P",
                        number = it.replace("[^a-zA-Z0-9]+", ""),
                        country = "PL",
                        expDate = LocalDate.now()
                    )
                )
            }
            addresses.add(
                Address(
                    addressType = AddressType.PERMANENT,
                    city = excelFileApplicant.address.city?.trim(),
                    cityIsCity = false,
                    countryCode = excelFileApplicant.address.countryCode?.trim(),
                    street = excelFileApplicant.address.street?.trim(),
                    streetNumber = excelFileApplicant.address.streetNumber?.trim(),
                    flatNumber = excelFileApplicant.address.flatNumber?.trim(),
                    postalCode = excelFileApplicant.address.postalCode?.trimPostalCode()
                )
            )
        }
    }
}
