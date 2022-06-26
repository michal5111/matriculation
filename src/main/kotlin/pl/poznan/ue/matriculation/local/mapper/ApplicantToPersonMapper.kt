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
            name = applicant.given,
            middleName = applicant.middle,
            surname = applicant.family,
            citizenship = applicant.citizenship?.let {
                citizenshipRepository.getById(it)
            },
            birthDate = applicant.dateOfBirth,
            birthCity = applicant.cityOfBirth,
            birthCountry = applicant.countryOfBirth?.let {
                citizenshipRepository.getById(it)
            },
            pesel = applicant.pesel,
            sex = applicant.sex,
            nationality = applicant.nationality?.let {
                citizenshipRepository.getById(it)
            },
            organizationalUnit = organizationalUnitRepository.getById(defaultStudentOrganizationalUnitString),
            middleSchool = applicant.highSchoolUsosCode?.let {
                schoolRepository.getById(it)
            },
            idNumber = applicant.primaryIdentityDocument?.number,
            documentType = applicant.primaryIdentityDocument?.number?.let {
                applicant.primaryIdentityDocument?.type
            },
            identityDocumentExpirationDate = applicant.primaryIdentityDocument?.expDate,
            identityDocumentIssuerCountry = applicant.primaryIdentityDocument?.country?.let {
                citizenshipRepository.getById(it)
            },
            mothersName = applicant.mothersName,
            fathersName = applicant.fathersName,
            wku = applicant.wku?.let {
                wkuRepository.getById(it)
            },
            militaryCategory = applicant.militaryCategory,
            militaryStatus = applicant.militaryStatus,
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
                val foundAddress = addresses.find { it.addressType.code == "POB" }
                    ?: addresses.find { it.addressType.code == "STA" }
                foundAddress?.let {
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
            applicant.documents.filter { document ->
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
