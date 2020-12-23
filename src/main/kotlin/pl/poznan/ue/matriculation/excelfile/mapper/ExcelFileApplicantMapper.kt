package pl.poznan.ue.matriculation.excelfile.mapper

import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicantDto
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
            casPasswordOverride = null,
            indexNumber = null,
            modificationDate = Date(),
            phone = excelFileApplicant.phoneNumber,
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
                    postalCode = excelFileApplicant.address.postalCode
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
                given = excelFileApplicant.given
                middle = excelFileApplicant.middle
                family = excelFileApplicant.family
            }
            email = excelFileApplicant.email
            basicData.apply {
                cityOfBirth = excelFileApplicant.birthPlace
                countryOfBirth = null
                dataSource = "User"
                dateOfBirth = excelFileApplicant.birthDate
                pesel = excelFileApplicant.pesel
                sex = excelFileApplicant.sex
            }
            additionalData.apply {
                fathersName = excelFileApplicant.fathersName
                mothersName = excelFileApplicant.mothersName
            }
            citizenship = excelFileApplicant.citizenship
            modificationDate = Date()
            phone = excelFileApplicant.phoneNumber
            phoneNumbers.clear()
            identityDocuments.clear()
            addresses.clear()
            excelFileApplicant.phoneNumber?.let {
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
                        number = it,
                        country = "PL",
                        expDate = Date()
                    )
                )
            }
            addresses.add(
                Address(
                    addressType = AddressType.PERMANENT,
                    city = excelFileApplicant.address.city,
                    cityIsCity = false,
                    countryCode = excelFileApplicant.address.countryCode,
                    street = excelFileApplicant.address.street,
                    streetNumber = excelFileApplicant.address.streetNumber,
                    flatNumber = excelFileApplicant.address.flatNumber,
                    postalCode = excelFileApplicant.address.postalCode
                )
            )
        }
    }
}