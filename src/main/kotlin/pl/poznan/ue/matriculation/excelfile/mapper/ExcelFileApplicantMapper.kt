package pl.poznan.ue.matriculation.excelfile.mapper

import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicantDto
import pl.poznan.ue.matriculation.kotlinExtensions.nameCapitalize
import pl.poznan.ue.matriculation.kotlinExtensions.trimPhoneNumber
import pl.poznan.ue.matriculation.kotlinExtensions.trimPostalCode
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.local.domain.enum.AddressType
import java.util.*

class ExcelFileApplicantMapper {

    fun mapExcelFileApplicantToApplicant(excelFileApplicant: ExcelFileApplicantDto): Applicant {
        return Applicant(
            foreignId = excelFileApplicant.id,
            name = Name(
                given = excelFileApplicant.given,
                middle = excelFileApplicant.middle,
                family = excelFileApplicant.family,
                maiden = null
            ),
            email = excelFileApplicant.email,
            basicData = BasicData(
                cityOfBirth = excelFileApplicant.birthPlace,
                countryOfBirth = null,
                dataSource = "User",
                dateOfBirth = excelFileApplicant.birthDate,
                pesel = excelFileApplicant.pesel,
                sex = excelFileApplicant.sex
            ),
            additionalData = AdditionalData(
                fathersName = excelFileApplicant.fathersName,
                mothersName = excelFileApplicant.mothersName,
                militaryCategory = null,
                militaryStatus = null,
                wku = null
            ),
            citizenship = excelFileApplicant.citizenship,
            applicantForeignerData = null,
            educationData = EducationData(),
            indexNumber = null,
            modificationDate = Date(),
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
                        type = 'P',
                        number = it,
                        country = "PL",
                        expDate = Date()
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
            name.applicant = this
            additionalData.applicant = this
            basicData.applicant = this
            educationData.applicant = this
        }
    }

    fun updateApplicantFromExcelApplicantDto(
        applicant: Applicant,
        excelFileApplicant: ExcelFileApplicantDto
    ): Applicant {
        return applicant.apply {
            name.apply {
                given = excelFileApplicant.given.nameCapitalize()
                middle = excelFileApplicant.middle?.nameCapitalize()
                family = excelFileApplicant.family.nameCapitalize()
            }
            email = excelFileApplicant.email.trim()
            basicData.apply {
                cityOfBirth = excelFileApplicant.birthPlace.trim()
                countryOfBirth = null
                dataSource = "User"
                dateOfBirth = excelFileApplicant.birthDate
                pesel = excelFileApplicant.pesel?.trim()
                sex = excelFileApplicant.sex
            }
            additionalData.apply {
                fathersName = excelFileApplicant.fathersName?.nameCapitalize()
                mothersName = excelFileApplicant.mothersName?.nameCapitalize()
            }
            citizenship = excelFileApplicant.citizenship.trim()
            modificationDate = Date()
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
                        type = 'P',
                        number = it.replace("[^a-zA-Z0-9]+", ""),
                        country = "PL",
                        expDate = Date()
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
