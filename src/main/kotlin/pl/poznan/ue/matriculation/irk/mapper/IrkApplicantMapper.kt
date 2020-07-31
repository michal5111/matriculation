package pl.poznan.ue.matriculation.irk.mapper

import pl.poznan.ue.matriculation.irk.dto.applicants.ContactDataDTO
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.kotlinExtensions.nameCapitalize
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.local.domain.enum.AddressType

class IrkApplicantMapper {

    fun mapApplicantDtoToApplicant(applicantDto: IrkApplicantDto): Applicant {
        return Applicant(
                foreignId = applicantDto.id,
                email = applicantDto.email,
                indexNumber = applicantDto.indexNumber,
                password = applicantDto.password,
                name = Name(
                        middle = applicantDto.name.middle?.nameCapitalize(),
                        family = applicantDto.name.family.nameCapitalize(),
                        maiden = applicantDto.name.maiden?.nameCapitalize(),
                        given = applicantDto.name.given.nameCapitalize()
                ),
                phone = applicantDto.phone,
                citizenship = applicantDto.citizenship,
                photo = applicantDto.photo,
                photoPermission = applicantDto.photoPermission,
                casPasswordOverride = applicantDto.casPasswordOverride,
                modificationDate = applicantDto.modificationDate,
                basicData = BasicData(
                        cityOfBirth = applicantDto.basicData.cityOfBirth,
                        countryOfBirth = applicantDto.basicData.countryOfBirth,
                        dataSource = applicantDto.basicData.dataSource,
                        dateOfBirth = applicantDto.basicData.dateOfBirth,
                        pesel = applicantDto.basicData.pesel,
                        sex = applicantDto.basicData.sex
                ),
                additionalData = applicantDto.additionalData.let {
                    AdditionalData(
                            countryOfBirth = it.countryOfBirth,
                            cityOfBirth = it.cityOfBirth,
                            documentCountry = it.documentCountry,
                            documentExpDate = it.documentExpDate,
                            documentNumber = it.documentNumber,
                            documentType = it.documentType,
                            fathersName = it.fathersName,
                            militaryCategory = it.militaryCategory,
                            militaryStatus = it.militaryStatus,
                            mothersName = it.mothersName,
                            wku = it.wku
                    )
                },
                applicantForeignerData = applicantDto.foreignerData?.let {
                    ApplicantForeignerData(
                            baseOfStay = it.baseOfStay,
                            foreignerStatus = it.foreignerStatus.map { statusDto ->
                                Status(
                                        status = statusDto
                                )
                            }.toMutableList(),
                            polishCardIssueCountry = it.polishCardIssueCountry,
                            polishCardIssueDate = it.polishCardIssueDate,
                            polishCardNumber = it.polishCardNumber,
                            polishCardValidTo = it.polishCardValidTo
                    )
                },
                educationData = applicantDto.educationData.let {
                    EducationData(
                            documents = it.documents.filter { document ->
                                document.issueDate != null && !document.documentNumber.isNullOrBlank()
                            }.map { documentDTO ->
                                Document(
                                        certificateType = documentDTO.certificateType,
                                        certificateTypeCode = documentDTO.certificateTypeCode,
                                        certificateUsosCode = documentDTO.certificateUsosCode,
                                        comment = documentDTO.comment,
                                        documentNumber = documentDTO.documentNumber!!,
                                        documentYear = documentDTO.documentYear,
                                        issueCity = documentDTO.issueCity,
                                        issueCountry = documentDTO.issueCountry,
                                        issueDate = documentDTO.issueDate!!,
                                        issueInstitution = documentDTO.issueInstitution,
                                        issueInstitutionUsosCode = documentDTO.issueInstitutionUsosCode,
                                        modificationDate = documentDTO.modificationDate
                                )
                            }.toMutableList(),
                            highSchoolCity = it.highSchoolCity,
                            highSchoolName = it.highSchoolName,
                            highSchoolType = it.highSchoolType,
                            highSchoolUsosCode = it.highSchoolUsosCode
                    )
                }
//                photoByteArray = applicantDTO.photo?.let {
//                    irkService.getPhoto(it)
//                }
        ).apply {
            additionalData.applicant = this
            basicData.applicant = this
            educationData.applicant = this
            educationData.documents.forEach { document ->
                document.educationData = educationData
            }
            applicantForeignerData?.applicant = this
            name.applicant = this
            addPhoneNumbers(this, applicantDto.contactData)
            addAddresses(this, applicantDto.contactData)
        }
    }

    fun update(applicant: Applicant, applicantDto: IrkApplicantDto): Applicant {
        applicant.apply {
            email = applicantDto.email
            indexNumber = applicantDto.indexNumber
            password = applicantDto.password
            name.apply {
                middle = applicantDto.name.middle?.nameCapitalize()
                family = applicantDto.name.family.nameCapitalize()
                maiden = applicantDto.name.maiden?.nameCapitalize()
                given = applicantDto.name.given.nameCapitalize()
            }
            phone = applicantDto.phone
            citizenship = applicantDto.citizenship
            photo = applicantDto.photo
            photoPermission = applicantDto.photoPermission
            casPasswordOverride = applicantDto.casPasswordOverride
            modificationDate = applicantDto.modificationDate
            basicData.apply {
                cityOfBirth = applicantDto.basicData.cityOfBirth
                countryOfBirth = applicantDto.basicData.countryOfBirth
                dataSource = applicantDto.basicData.dataSource
                dateOfBirth = applicantDto.basicData.dateOfBirth
                pesel = applicantDto.basicData.pesel
                sex = applicantDto.basicData.sex
            }
            applicantDto.contactData.let {
                addresses.clear()
                addAddresses(applicant, it)
                phoneNumbers.clear()
                addPhoneNumbers(applicant, it)
            }
            applicantDto.additionalData.let {
                applicant.additionalData.apply {
                    countryOfBirth = it.countryOfBirth
                    cityOfBirth = it.cityOfBirth
                    documentCountry = it.documentCountry
                    documentExpDate = it.documentExpDate
                    documentNumber = it.documentNumber
                    documentType = it.documentType
                    fathersName = it.fathersName
                    militaryCategory = it.militaryCategory
                    militaryStatus = it.militaryStatus
                    mothersName = it.mothersName
                    wku = it.wku
                }
            }
            applicantDto.foreignerData?.let {
                applicant.applicantForeignerData?.apply {
                    baseOfStay = it.baseOfStay
                    foreignerStatus.clear()
                    foreignerStatus.addAll(it.foreignerStatus.map { statusDto ->
                        Status(
                                status = statusDto
                        )
                    })
                    polishCardIssueCountry = it.polishCardIssueCountry
                    polishCardIssueDate = it.polishCardIssueDate
                    polishCardNumber = it.polishCardNumber
                    polishCardValidTo = it.polishCardValidTo
                }
            }
            applicantDto.educationData.let {
                applicant.educationData.apply {
                    documents.clear()
                    documents.addAll(it.documents.filter { document ->
                        document.issueDate != null && !document.documentNumber.isNullOrBlank()
                    }.map { documentDto ->
                        Document(
                                educationData = this,
                                certificateType = documentDto.certificateType,
                                certificateTypeCode = documentDto.certificateTypeCode,
                                certificateUsosCode = documentDto.certificateUsosCode,
                                comment = documentDto.comment,
                                documentNumber = documentDto.documentNumber!!,
                                documentYear = documentDto.documentYear,
                                issueCity = documentDto.issueCity,
                                issueCountry = documentDto.issueCountry,
                                issueDate = documentDto.issueDate!!,
                                issueInstitution = documentDto.issueInstitution,
                                issueInstitutionUsosCode = documentDto.issueInstitutionUsosCode,
                                modificationDate = documentDto.modificationDate
                        )
                    })
                    highSchoolCity = it.highSchoolCity
                    highSchoolName = it.highSchoolName
                    highSchoolType = it.highSchoolType
                    highSchoolUsosCode = it.highSchoolUsosCode
                }
            }
        }
        return applicant
    }

    private fun addPhoneNumbers(applicant: Applicant, contactDataDTO: ContactDataDTO) {
        contactDataDTO.phoneNumber?.let {
            applicant.phoneNumbers.add(
                    PhoneNumber(
                            number = contactDataDTO.phoneNumber,
                            phoneNumberType = contactDataDTO.phoneNumberType!!,
                            comment = "Podstawowy numer telefonu",
                            applicant = applicant
                    )
            )
        }
        if (contactDataDTO.phoneNumber == contactDataDTO.phoneNumber2) {
            return
        }
        contactDataDTO.phoneNumber2?.let {
            applicant.phoneNumbers.add(
                    PhoneNumber(
                            number = contactDataDTO.phoneNumber2,
                            phoneNumberType = contactDataDTO.phoneNumber2Type!!,
                            comment = "Alternatywny numer telefonu",
                            applicant = applicant
                    )
            )
        }
    }

    private fun addAddresses(applicant: Applicant, contactDataDTO: ContactDataDTO) {
        contactDataDTO.let {
            if (!(it.officialCity.isNullOrBlank()
                            || it.officialStreet.isNullOrBlank()
                            || it.officialCountry.isNullOrBlank())) {
                applicant.addresses.add(
                        Address(
                                addressType = AddressType.PERMANENT,
                                city = it.officialCity,
                                cityIsCity = it.officialCityIsCity,
                                countryCode = it.officialCountry,
                                flatNumber = it.officialFlatNumber,
                                postalCode = it.officialPostCode,
                                street = it.officialStreet,
                                streetNumber = it.officialStreetNumber,
                                applicant = applicant
                        )
                )
            }
            if (!(it.realCity.isNullOrBlank()
                            || it.realStreet.isNullOrBlank()
                            || it.realCountry.isNullOrBlank())) {
                applicant.addresses.add(
                        Address(
                                addressType = AddressType.RESIDENCE,
                                city = it.realCity,
                                cityIsCity = it.realCityIsCity,
                                countryCode = it.realCountry,
                                flatNumber = it.realFlatNumber,
                                postalCode = it.realPostCode,
                                street = it.realStreet,
                                streetNumber = it.realStreetNumber,
                                applicant = applicant
                        )
                )
            }
        }
    }
}