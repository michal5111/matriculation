package pl.poznan.ue.matriculation.irk.mapper

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.irk.dto.applicants.ApplicantDTO
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.repo.*
import pl.poznan.ue.matriculation.oracle.service.AddressService
import javax.annotation.PostConstruct

@Component
class ApplicantMapper(
        private val citizenshipRepository: CitizenshipRepository,
        private val organizationalUnitRepository: OrganizationalUnitRepository,
        private val schoolRepository: SchoolRepository,
        private val addressTypeRepository: AddressTypeRepository,
        private val phoneNumberTypeRepository: PhoneNumberTypeRepository,
        private val wkuRepository: WkuRepository,
        private val irkService: IrkService,
        private val addressService: AddressService,
        private val documentTypeRepository: DocumentTypeRepository
) {

    lateinit var defaultStudentOrganizationalUnit: OrganizationalUnit
    lateinit var permanentAddressType: AddressType
    lateinit var correspondenceAddressType: AddressType

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitString: String

    @Value("\${pl.poznan.ue.matriculation.universityEmailSuffix}")
    lateinit var universityEmailSuffix: String

    @PostConstruct
    fun init() {
        defaultStudentOrganizationalUnit = organizationalUnitRepository.getOne(defaultStudentOrganizationalUnitString)
        permanentAddressType = addressTypeRepository.getOne("STA")
        correspondenceAddressType = addressTypeRepository.getOne("KOR")
    }

    fun applicantDtoToApplicantMapper(applicantDTO: ApplicantDTO): Applicant {
        return Applicant(
                irkId = applicantDTO.id,
                email = applicantDTO.email,
                indexNumber = applicantDTO.indexNumber,
                password = applicantDTO.password,
                name = Name(
                        middle = applicantDTO.name.middle,
                        family = applicantDTO.name.family,
                        maiden = applicantDTO.name.maiden,
                        given = applicantDTO.name.given
                ),
                phone = applicantDTO.phone,
                citizenship = applicantDTO.citizenship,
                photo = applicantDTO.photo,
                photoPermission = applicantDTO.photoPermission,
                casPasswordOverride = applicantDTO.casPasswordOverride,
                modificationDate = applicantDTO.modificationDate,
                basicData = BasicData(
                        cityOfBirth = applicantDTO.basicData.cityOfBirth,
                        countryOfBirth = applicantDTO.basicData.countryOfBirth,
                        dataSource = applicantDTO.basicData.dataSource,
                        dateOfBirth = applicantDTO.basicData.dateOfBirth,
                        pesel = applicantDTO.basicData.pesel,
                        sex = applicantDTO.basicData.sex
                ),
                contactData = applicantDTO.contactData.let {
                    ContactData(
                            modificationDate = it.modificationDate,
                            officialCity = it.officialCity,
                            officialCityIsCity = it.officialCityIsCity,
                            officialCountry = it.officialCountry,
                            officialFlatNumber = it.officialFlatNumber,
                            officialPostCode = it.officialPostCode,
                            officialStreet = it.officialStreet,
                            officialStreetNumber = it.officialStreetNumber,
                            phoneNumber = it.phoneNumber,
                            phoneNumber2 = it.phoneNumber2,
                            phoneNumberType = it.phoneNumberType,
                            phoneNumber2Type = it.phoneNumber2Type,
                            realCity = it.realCity,
                            realCityIsCity = it.realCityIsCity,
                            realCountry = it.realCountry,
                            realFlatNumber = it.realFlatNumber,
                            realPostCode = it.realPostCode,
                            realStreet = it.realStreet,
                            realStreetNumber = it.realStreetNumber
                    )
                },
                additionalData = applicantDTO.additionalData.let {
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
                applicantForeignerData = applicantDTO.foreignerData?.let {
                    ApplicantForeignerData(
                            baseOfStay = it.baseOfStay,
                            foreignerStatus = it.foreignerStatus.map { statusDto ->
                                Status(
                                        status = statusDto
                                )
                            },
                            polishCardIssueCountry = it.polishCardIssueCountry,
                            polishCardIssueDate = it.polishCardIssueDate,
                            polishCardNumber = it.polishCardNumber,
                            polishCardValidTo = it.polishCardValidTo
                    )
                },
                educationData = applicantDTO.educationData.let {
                    EducationData(
                            documents = it.documents.map { documentDTO ->
                                Document(
                                        certificateType = documentDTO.certificateType,
                                        certificateTypeCode = documentDTO.certificateTypeCode,
                                        certificateUsosCode = documentDTO.certificateUsosCode,
                                        comment = documentDTO.comment,
                                        documentNumber = documentDTO.documentNumber,
                                        documentYear = documentDTO.documentYear,
                                        issueCity = documentDTO.issueCity,
                                        issueCountry = documentDTO.issueCountry,
                                        issueDate = documentDTO.issueDate,
                                        issueInstitution = documentDTO.issueInstitution,
                                        issueInstitutionUsosCode = documentDTO.issueInstitutionUsosCode,
                                        modificationDate = documentDTO.modificationDate
                                )
                            }.filter { document ->
                                document.issueDate != null && !document.documentNumber.isNullOrBlank()
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
            contactData.applicant = this
            educationData.applicant = this
            educationData.documents.forEach { document ->
                document.educationData = educationData
            }
            applicantForeignerData?.applicant = this
            name.applicant = this
        }
    }

    fun applicantToPersonMapper(applicant: Applicant): Person {
        return Person(
                email = applicant.email.takeIf {
                    it.endsWith(universityEmailSuffix)
                },
                privateEmail = applicant.email.takeIf {
                    !it.endsWith(universityEmailSuffix)
                },
                name = applicant.name.given!!,
                middleName = applicant.name.middle,
                surname = applicant.name.family!!,
                citizenship = citizenshipRepository.getOne(applicant.citizenship!!),
                birthDate = applicant.basicData.dateOfBirth,
                birthCity = applicant.basicData.cityOfBirth,
                birthCountry = citizenshipRepository.getOne(applicant.basicData.countryOfBirth),
                pesel = applicant.basicData.pesel,
                sex = applicant.basicData.sex,
                nationality = citizenshipRepository.getOne(applicant.basicData.countryOfBirth),
                organizationalUnit = defaultStudentOrganizationalUnit,
                middleSchool = applicant.educationData.highSchoolUsosCode?.let {
                    schoolRepository.getOne(it)
                },
                addresses = listOf(
                        addressService.create(
                                addressType = permanentAddressType,
                                city = applicant.contactData.officialCity,
                                street = applicant.contactData.officialStreet,
                                houseNumber = applicant.contactData.officialStreetNumber,
                                apartmentNumber = applicant.contactData.officialFlatNumber,
                                zipCode = applicant.contactData.officialPostCode,
                                cityIsCity = applicant.contactData.officialCityIsCity,
                                countryCode = applicant.contactData.officialCountry
                        ),
                        addressService.create(
                                addressType = correspondenceAddressType,
                                city = applicant.contactData.realCity,
                                street = applicant.contactData.realStreet,
                                houseNumber = applicant.contactData.realStreetNumber,
                                apartmentNumber = applicant.contactData.realFlatNumber,
                                zipCode = applicant.contactData.realPostCode,
                                cityIsCity = applicant.contactData.realCityIsCity,
                                countryCode = applicant.contactData.realCountry
                        )
                ).filter {
                    !(it.city.isNullOrBlank() or
                            it.street.isNullOrBlank() or
                            it.houseNumber.isNullOrBlank() or
                            it.zipCode.isNullOrBlank())
                }.toMutableList(),
                phoneNumbers = getPhoneNumberList(applicant.contactData),
                idNumber = applicant.additionalData.documentNumber,
                documentType = applicant.additionalData.documentNumber?.let {
                    applicant.additionalData.documentType
                },
                identityDocumentExpirationDate = applicant.additionalData.documentExpDate,
                identityDocumentIssuerCountry = applicant.additionalData.documentCountry?.let {
                    citizenshipRepository.getOne(it)
                },
                mothersName = applicant.additionalData.mothersName,
                fathersName = applicant.additionalData.fathersName,
                wku = applicant.additionalData.wku?.let {
                    wkuRepository.getOne(it)
                },
                entitlementDocuments = applicant.educationData.documents.filter { document ->
                    //document.certificateTypeCode in listOf("D","L","M","I","N","B","Z","U","K","E","R","G")
                    document.certificateUsosCode != null
                }.map {
                    EntitlementDocument(
                            issueDate = it.issueDate!!,
                            description = it.certificateType,
                            number = it.documentNumber!!,
                            type = it.certificateUsosCode!!,
                            school = it.issueInstitutionUsosCode?.let { schoolId ->
                                schoolId.toLongOrNull()?.let { schoolIdLong ->
                                    schoolRepository.getOne(schoolIdLong)
                                }
                            }
                    )
                }.toMutableList(),
                militaryCategory = applicant.additionalData.militaryCategory,
                militaryStatus = applicant.additionalData.militaryStatus,
                personPhoto = applicant.photo?.let {
                    PersonPhoto(
                            photoBlob = irkService.getPhoto(it)
                    )
                }
        ).apply {
            addresses.forEach {
                it.person = this
            }
            phoneNumbers.forEach {
                it.person = this
            }
            entitlementDocuments.forEach {
                it.person = this
            }
            personPhoto?.let {
                it.person = this
                personPreferences.add(
                        PersonPreference(this, "photo_visibility", applicant.photoPermission)
                )
            }
            addresses.forEach {
                it.person = this
            }
            applicant.applicantForeignerData?.let {
                if (it.baseOfStay != null && it.baseOfStay == "OKS") {
                    ownedDocuments.add(
                            OwnedDocument(
                                    documentType = documentTypeRepository.getOne(it.baseOfStay!!),
                                    person = this,
                                    issueDate = it.polishCardIssueDate,
                                    issueCountry = it.polishCardIssueCountry?.let { countryCode ->
                                        citizenshipRepository.getOne(countryCode)
                                    },
                                    number = it.polishCardNumber,
                                    expirationDate = it.polishCardValidTo
                            )
                    )
                }
            }
        }
    }

    private fun getPhoneNumberList(contactData: ContactData): MutableList<PhoneNumber> {
        val phoneNumbers: MutableList<PhoneNumber> = mutableListOf()
        contactData.phoneNumberType?.run {
            phoneNumbers.add(
                    PhoneNumber(
                            phoneNumberType = phoneNumberTypeRepository.getOne(this),
                            number = contactData.phoneNumber!!,
                            comments = "Podstawowy numer telefonu"
                    )
            )
        }
        contactData.phoneNumber2Type?.run {
            if (contactData.phoneNumber2 != contactData.phoneNumber) {
                phoneNumbers.add(
                        PhoneNumber(
                                phoneNumberType = phoneNumberTypeRepository.getOne(this),
                                number = contactData.phoneNumber2!!,
                                comments = "Alternatywny numer telefonu"
                        )
                )
            }
        }
        if (phoneNumbers.size > 1 && phoneNumbers[0].number == phoneNumbers[1].number) {
            return mutableListOf()
        }
        return phoneNumbers
    }
}