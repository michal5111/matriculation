package pl.poznan.ue.matriculation.local.mapper

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
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
    private val documentTypeRepository: DocumentTypeRepository,
    private val addressTypeRepository: AddressTypeRepository
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
            citizenship = citizenshipRepository.getOne(applicant.citizenship),
            birthDate = applicant.basicData.dateOfBirth,
            birthCity = applicant.basicData.cityOfBirth,
            birthCountry = applicant.basicData.countryOfBirth?.let {
                citizenshipRepository.getOne(it)
            },
            pesel = applicant.basicData.pesel,
            sex = applicant.basicData.sex,
            nationality = applicant.nationality?.let {
                citizenshipRepository.getOne(it)
            },
            organizationalUnit = organizationalUnitRepository.getOne(defaultStudentOrganizationalUnitString),
            middleSchool = applicant.educationData.highSchoolUsosCode?.let {
                schoolRepository.getOne(it)
            },
            addresses = applicant.addresses.map {
                addressService.create(
                    addressType = addressTypeRepository.getOne(it.addressType.usosValue),
                    city = it.city,
                    street = it.street,
                    houseNumber = it.streetNumber,
                    apartmentNumber = it.flatNumber,
                    zipCode = it.postalCode,
                    cityIsCity = it.cityIsCity,
                    countryCode = it.countryCode
                )
            }.filterNot {
                it.city.isNullOrBlank() || it.street.isNullOrBlank()
            }.toMutableList(),
            phoneNumbers = applicant.phoneNumbers.map {
                PhoneNumber(
                    number = it.number,
                    phoneNumberType = phoneNumberTypeRepository.getOne(it.phoneNumberType),
                    comments = it.comment
                )
            }.toMutableList(),
            idNumber = applicant.identityDocuments.firstOrNull()?.number?.replace(" ", "")?.trim(),
            documentType = applicant.identityDocuments.firstOrNull()?.number?.let {
                applicant.identityDocuments.firstOrNull()?.type
            },
            identityDocumentExpirationDate = applicant.identityDocuments.firstOrNull()?.expDate,
            identityDocumentIssuerCountry = applicant.identityDocuments.firstOrNull()?.country?.let {
                citizenshipRepository.getOne(it)
            },
            mothersName = applicant.additionalData.mothersName,
            fathersName = applicant.additionalData.fathersName,
            wku = applicant.additionalData.wku?.let {
                wkuRepository.getOne(it)
            },
            entitlementDocuments = applicant.educationData.documents.filter { document ->
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
                        schoolRepository.getOne(schoolId)
                    }
                )
            }.toMutableList(),
            militaryCategory = applicant.additionalData.militaryCategory,
            militaryStatus = applicant.additionalData.militaryStatus,
            personPhoto = applicant.photoByteArray?.let {
                PersonPhoto(
                    photoBlob = it
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
                    PersonPreference(person = this, attribute = "photo_visibility", value = applicant.photoPermission)
                )
            }
            applicant.applicantForeignerData?.let {
                if (it.baseOfStay == null || it.baseOfStay != "OKP") {
                    return@let
                }
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