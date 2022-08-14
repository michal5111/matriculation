package pl.poznan.ue.matriculation.irk.mapper

import pl.poznan.ue.matriculation.irk.dto.applicants.AdditionalDataDTO
import pl.poznan.ue.matriculation.irk.dto.applicants.ContactDataDTO
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.kotlinExtensions.trimPhoneNumber
import pl.poznan.ue.matriculation.kotlinExtensions.trimPostalCode
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.local.domain.const.PhoneNumberType
import pl.poznan.ue.matriculation.local.domain.enum.AddressType

class IrkApplicantMapper {

    fun mapApplicantDtoToApplicant(applicantDto: IrkApplicantDto): Applicant {
        return Applicant(
            foreignId = applicantDto.id,
            email = applicantDto.email.trim(),
            indexNumber = applicantDto.indexNumber?.trim(),
            password = applicantDto.password,
            middle = applicantDto.name.middle,
            family = applicantDto.name.family,
            maiden = applicantDto.name.maiden,
            given = applicantDto.name.given,
            citizenship = applicantDto.citizenship,
            photo = applicantDto.photo,
            photoPermission = applicantDto.photoPermission,
            modificationDate = applicantDto.modificationDate,
            cityOfBirth = applicantDto.basicData.cityOfBirth?.trim(),
            countryOfBirth = applicantDto.basicData.countryOfBirth,
            dateOfBirth = applicantDto.basicData.dateOfBirth,
            pesel = applicantDto.basicData.pesel?.trim(),
            sex = applicantDto.basicData.sex,
            fathersName = applicantDto.additionalData.fathersName?.trim(),
            militaryCategory = applicantDto.additionalData.militaryCategory,
            militaryStatus = applicantDto.additionalData.militaryStatus,
            mothersName = applicantDto.additionalData.mothersName?.trim(),
            wku = applicantDto.additionalData.wku,
            applicantForeignerData = applicantDto.foreignerData?.let {
                ApplicantForeignerData(
                    baseOfStay = it.baseOfStay,
                    foreignerStatus = it.foreignerStatus.map { statusDto ->
                        Status(
                            status = statusDto
                        )
                    }.toMutableSet(),
                    polishCardIssueCountry = it.polishCardIssueCountry,
                    polishCardIssueDate = it.polishCardIssueDate,
                    polishCardNumber = it.polishCardNumber?.trim(),
                    polishCardValidTo = it.polishCardValidTo
                )
            },
            highSchoolCity = applicantDto.educationData.highSchoolCity?.trim(),
            highSchoolName = applicantDto.educationData.highSchoolName?.trim(),
            highSchoolType = applicantDto.educationData.highSchoolType,
            highSchoolUsosCode = applicantDto.educationData.highSchoolUsosCode
        ).apply {
            addDocuments(this, applicantDto)
            applicantForeignerData?.applicant = this
            addPhoneNumbers(this, applicantDto.contactData)
            addAddresses(this, applicantDto.contactData)
            addIdentityDocuments(this, applicantDto.additionalData)
        }
    }

    private fun addDocuments(applicant: Applicant, applicantDto: IrkApplicantDto) {
        applicantDto.educationData.documents.filter { document ->
            document.issueDate != null && !document.documentNumber.isNullOrBlank()
        }.map { documentDTO ->
            documentDTO.documentNumber
                ?: throw IllegalArgumentException("Document number is null")
            documentDTO.issueDate ?: throw IllegalArgumentException("Document issue date is null")
            Document(
                certificateType = documentDTO.certificateType,
                certificateTypeCode = documentDTO.certificateTypeCode,
                certificateUsosCode = documentDTO.certificateUsosCode,
                comment = documentDTO.comment,
                documentNumber = documentDTO.documentNumber.trim(),
                documentYear = documentDTO.documentYear,
                issueCity = documentDTO.issueCity?.trim(),
                issueCountry = documentDTO.issueCountry,
                issueDate = documentDTO.issueDate,
                issueInstitution = documentDTO.issueInstitution?.trim(),
                issueInstitutionUsosCode = documentDTO.issueInstitutionUsosCode,
                modificationDate = documentDTO.modificationDate
            )
        }.forEach {
            applicant.addDocument(it)
        }
    }

    fun update(applicant: Applicant, applicantDto: IrkApplicantDto): Applicant = applicant.apply {
        email = applicantDto.email
        indexNumber = applicantDto.indexNumber
        password = applicantDto.password
        middle = applicantDto.name.middle
        family = applicantDto.name.family
        maiden = applicantDto.name.maiden
        given = applicantDto.name.given
        citizenship = applicantDto.citizenship
        photo = applicantDto.photo
        photoPermission = applicantDto.photoPermission
        modificationDate = applicantDto.modificationDate
        cityOfBirth = applicantDto.basicData.cityOfBirth?.trim()
        countryOfBirth = applicantDto.basicData.countryOfBirth
        dateOfBirth = applicantDto.basicData.dateOfBirth
        pesel = applicantDto.basicData.pesel?.trim()
        sex = applicantDto.basicData.sex
        applicantDto.contactData.let {
            addAddresses(applicant, it)
            addPhoneNumbers(applicant, it)
        }
        fathersName = applicantDto.additionalData.fathersName?.trim()
        militaryCategory = applicantDto.additionalData.militaryCategory
        militaryStatus = applicantDto.additionalData.militaryStatus
        mothersName = applicantDto.additionalData.mothersName?.trim()
        wku = applicantDto.additionalData.wku
        applicantDto.foreignerData?.let {
            applicant.applicantForeignerData?.apply {
                baseOfStay = it.baseOfStay
                foreignerStatus.addAll(it.foreignerStatus.map { statusDto ->
                    Status(
                        status = statusDto
                    )
                })
                polishCardIssueCountry = it.polishCardIssueCountry
                polishCardIssueDate = it.polishCardIssueDate
                polishCardNumber = it.polishCardNumber?.trim()
                polishCardValidTo = it.polishCardValidTo
            }
        }
        applicantDto.educationData.let {
            addDocuments(this, applicantDto)
            highSchoolCity = it.highSchoolCity?.trim()
            highSchoolName = it.highSchoolName?.trim()
            highSchoolType = it.highSchoolType
            highSchoolUsosCode = it.highSchoolUsosCode
        }
        addIdentityDocuments(applicant, applicantDto.additionalData)
    }

    private fun addIdentityDocuments(applicant: Applicant, additionalDataDTO: AdditionalDataDTO) {
        additionalDataDTO.let {
            mutableListOf(
                IdentityDocument(
                    country = it.documentCountry,
                    expDate = it.documentExpDate,
                    number = it.documentNumber?.replace("[^a-zA-Z0-9]+", ""),
                    type = it.documentType,
                    applicant = applicant
                )
            )
        }.filterNot { identityDocument ->
            identityDocument.number.isNullOrBlank()
        }.forEach {
            applicant.addIdentityDocument(it)
        }
    }

    private fun addPhoneNumbers(applicant: Applicant, contactDataDTO: ContactDataDTO) {
        contactDataDTO.phoneNumber?.trimPhoneNumber()?.let {
            applicant.addPhoneNumber(
                PhoneNumber(
                    number = it,
                    phoneNumberType = contactDataDTO.phoneNumberType ?: PhoneNumberType.MOBILE,
                    comment = "Podstawowy numer telefonu"
                )
            )
        }
        if (contactDataDTO.phoneNumber == contactDataDTO.phoneNumber2) {
            return
        }
        contactDataDTO.phoneNumber2?.trimPhoneNumber()?.let {
            applicant.addPhoneNumber(
                PhoneNumber(
                    number = it,
                    phoneNumberType = contactDataDTO.phoneNumber2Type ?: PhoneNumberType.MOBILE,
                    comment = "Alternatywny numer telefonu"
                )
            )
        }
    }

    private fun addAddresses(applicant: Applicant, contactDataDTO: ContactDataDTO) {
        contactDataDTO.run {
            mutableListOf(
                Address(
                    addressType = AddressType.PERMANENT,
                    city = officialCity?.trim(),
                    cityIsCity = officialCityIsCity,
                    countryCode = officialCountry,
                    flatNumber = officialFlatNumber?.trim(),
                    postalCode = officialPostCode?.trimPostalCode(),
                    street = officialStreet?.trim(),
                    streetNumber = officialStreetNumber.trim()
                ),
                Address(
                    addressType = AddressType.RESIDENCE,
                    city = realCity?.trim(),
                    cityIsCity = realCityIsCity,
                    countryCode = realCountry,
                    flatNumber = realFlatNumber?.trim(),
                    postalCode = realPostCode?.trimPostalCode(),
                    street = realStreet?.trim(),
                    streetNumber = realStreetNumber?.trim()
                )
            ).filterNot {
                it.city.isNullOrBlank()
                    && it.countryCode.isNullOrBlank()
                    && it.flatNumber.isNullOrBlank()
                    && it.postalCode.isNullOrBlank()
                    && it.street.isNullOrBlank()
                    && it.streetNumber.isNullOrBlank()
            }.forEach {
                applicant.addAddress(it)
            }
        }
    }
}
