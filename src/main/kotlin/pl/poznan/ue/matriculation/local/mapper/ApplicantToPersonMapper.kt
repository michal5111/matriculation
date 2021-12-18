package pl.poznan.ue.matriculation.local.mapper

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.kotlinExtensions.toSerialBlob
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.repo.*
import pl.poznan.ue.matriculation.oracle.service.AddressService

@Component
class ApplicantToPersonMapper(
    private val citizenshipRepository: CitizenshipRepository,
    private val organizationalUnitRepository: OrganizationalUnitRepository,
    private val schoolRepository: SchoolRepository,
    private val phoneNumberTypeRepository: PhoneNumberTypeRepository,
    private val wkuRepository: WkuRepository,
    private val addressService: AddressService,
    private val documentTypeRepository: DocumentTypeRepository
) {

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitString: String

    @Value("\${pl.poznan.ue.matriculation.universityEmailSuffix}")
    lateinit var universityEmailSuffix: String

    fun map(applicant: Applicant): Person {
        return Person(
            email = applicant.email.takeIf {
                it.endsWith(universityEmailSuffix)
            },
            privateEmail = applicant.email.takeUnless {
                it.endsWith(universityEmailSuffix)
            },
            name = applicant.name.given,
            middleName = applicant.name.middle,
            surname = applicant.name.family,
            citizenship = citizenshipRepository.getById(applicant.citizenship),
            birthDate = applicant.basicData.dateOfBirth,
            birthCity = applicant.basicData.cityOfBirth,
            birthCountry = applicant.basicData.countryOfBirth?.let {
                citizenshipRepository.getById(it)
            },
            pesel = applicant.basicData.pesel,
            sex = applicant.basicData.sex,
            nationality = applicant.nationality?.let {
                citizenshipRepository.getById(it)
            },
            organizationalUnit = organizationalUnitRepository.getById(defaultStudentOrganizationalUnitString),
            middleSchool = applicant.educationData.highSchoolUsosCode?.let {
                schoolRepository.getById(it)
            },
            idNumber = applicant.identityDocuments.firstOrNull()?.number,
            documentType = applicant.identityDocuments.firstOrNull()?.number?.let {
                applicant.identityDocuments.firstOrNull()?.type
            },
            identityDocumentExpirationDate = applicant.identityDocuments.firstOrNull()?.expDate,
            identityDocumentIssuerCountry = applicant.identityDocuments.firstOrNull()?.country?.let {
                citizenshipRepository.getById(it)
            },
            mothersName = applicant.additionalData.mothersName,
            fathersName = applicant.additionalData.fathersName,
            wku = applicant.additionalData.wku?.let {
                wkuRepository.getById(it)
            },
            militaryCategory = applicant.additionalData.militaryCategory,
            militaryStatus = applicant.additionalData.militaryStatus,
            personPhoto = applicant.photoByteArrayFuture?.get()?.let {
                PersonPhoto(
                    photoBlob = it.toSerialBlob()
                )
            }
        ).apply {
            applicant.addresses.map {
                addressService.create(
                    addressTypeCode = it.addressType.usosValue,
                    city = it.city,
                    street = it.street,
                    houseNumber = it.streetNumber,
                    apartmentNumber = it.flatNumber,
                    zipCode = it.postalCode,
                    cityIsCity = it.cityIsCity,
                    countryCode = it.countryCode
                )
            }.forEach {
                addAddress(it)
            }
            if (addresses.none { it.addressType.code == "KOR" }) {
                addresses.find { it.addressType.code == "POB" } ?: addresses.find { it.addressType.code == "STA" }
                    ?.let {
                        val ca = addressService.create(
                            person = this,
                            addressTypeCode = "KOR",
                            city = it.city,
                            street = it.street,
                            houseNumber = it.houseNumber,
                            apartmentNumber = it.flatNumber,
                            zipCode = it.zipCode,
                            cityIsCity = it.cityIsCity,
                            countryCode = it.country?.code
                        )
                        addAddress(ca)
                    }
            }
            applicant.educationData.documents.filter { document ->
                document.certificateUsosCode != null
            }.map {
                EntitlementDocument(
                    issueDate = it.issueDate,
                    description = it.issueInstitution.takeIf { _ ->
                        it.issueInstitutionUsosCode == null
                    },
                    number = it.documentNumber,
                    type = it.certificateUsosCode!!,
                    school = it.issueInstitutionUsosCode?.let { schoolId ->
                        schoolRepository.getById(schoolId)
                    }
                )
            }.forEach {
                addEntitlementDocument(it)
            }
            applicant.phoneNumbers.map {
                PhoneNumber(
                    number = it.number,
                    phoneNumberType = phoneNumberTypeRepository.getById(it.phoneNumberType),
                    comments = it.comment
                )
            }.forEach {
                addPhoneNumber(it)
            }
            personPhoto?.let {
                it.person = this
                addPersonPreference(
                    PersonPreference(person = this, attribute = "photo_visibility", value = applicant.photoPermission)
                )
            }
            applicant.applicantForeignerData?.let {
                if (it.baseOfStay != "OKP") {
                    return@let
                }
                val od = OwnedDocument(
                    documentType = documentTypeRepository.getById(it.baseOfStay ?: return@let),
                    person = this,
                    issueDate = it.polishCardIssueDate,
                    issueCountry = it.polishCardIssueCountry?.let { countryCode ->
                        citizenshipRepository.getById(countryCode)
                    },
                    number = it.polishCardNumber,
                    expirationDate = it.polishCardValidTo
                )
                addOwnedDocument(od)
            }
        }
    }
}
