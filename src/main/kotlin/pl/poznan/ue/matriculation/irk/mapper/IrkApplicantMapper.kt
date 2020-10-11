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
            identityDocuments = applicantDto.additionalData.let {
                mutableListOf(
                        IdentityDocument(
                                country = it.documentCountry,
                                expDate = it.documentExpDate,
                                number = it.documentNumber?.replace(" ", ""),
                                type = it.documentType,
                                applicant = this
                        )
                )
            }
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
                    it.documents.filter { document ->
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
                    }.let {
                        documents.addAll(it)
                    }
                    highSchoolCity = it.highSchoolCity
                    highSchoolName = it.highSchoolName
                    highSchoolType = it.highSchoolType
                    highSchoolUsosCode = it.highSchoolUsosCode
                }
            }
            identityDocuments.clear()
            identityDocuments.add(
                    applicantDto.additionalData.let {
                        IdentityDocument(
                                country = it.documentCountry,
                                expDate = it.documentExpDate,
                                number = it.documentNumber,
                                type = it.documentType,
                                applicant = applicant
                        )
                    }
            )
        }
        return applicant
    }

    private fun addPhoneNumbers(applicant: Applicant, contactDataDTO: ContactDataDTO) {
        contactDataDTO.phoneNumber?.let {
            applicant.phoneNumbers.add(
                    PhoneNumber(
                            number = it,
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
                            number = it,
                            phoneNumberType = contactDataDTO.phoneNumber2Type!!,
                            comment = "Alternatywny numer telefonu",
                            applicant = applicant
                    )
            )
        }
    }

    private fun addAddresses(applicant: Applicant, contactDataDTO: ContactDataDTO) {
        contactDataDTO.run {
            mutableListOf(
                    Address(
                            addressType = AddressType.PERMANENT,
                            city = officialCity,
                            cityIsCity = officialCityIsCity,
                            countryCode = officialCountry,
                            flatNumber = officialFlatNumber,
                            postalCode = officialPostCode,
                            street = officialStreet,
                            streetNumber = officialStreetNumber,
                            applicant = applicant
                    ),
                    Address(
                            addressType = AddressType.RESIDENCE,
                            city = realCity,
                            cityIsCity = realCityIsCity,
                            countryCode = realCountry,
                            flatNumber = realFlatNumber,
                            postalCode = realPostCode,
                            street = realStreet,
                            streetNumber = realStreetNumber,
                            applicant = applicant
                    )
            ).let { list ->
                applicant.addresses.addAll(list)
            }
        }
    }
}