package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.kotlinExtensions.toSerialBlob
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.ApplicantForeignerData
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.entityRepresentations.PersonBasicData
import pl.poznan.ue.matriculation.oracle.repo.*
import java.util.*

@Service
class PersonService(
    private val personRepository: PersonRepository,
    private val citizenshipRepository: CitizenshipRepository,
    private val schoolRepository: SchoolRepository,
    private val wkuRepository: WkuRepository,
    private val phoneNumberTypeRepository: PhoneNumberTypeRepository,
    private val addressService: AddressService,
    private val documentTypeRepository: DocumentTypeRepository,
    private val ownedDocumentRepository: OwnedDocumentRepository,
    private val organizationalUnitRepository: OrganizationalUnitRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(PersonService::class.java)

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitString: String

    @Value("\${pl.poznan.ue.matriculation.universityEmailSuffix}")
    lateinit var universityEmailSuffix: String

    @LogExecutionTime
    fun create(applicant: Applicant): Person {
        val person = Person(
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
                citizenshipRepository.getReferenceById(it)
            },
            birthDate = applicant.dateOfBirth,
            birthCity = applicant.cityOfBirth,
            birthCountry = applicant.countryOfBirth?.let {
                citizenshipRepository.getReferenceById(it)
            },
            pesel = applicant.pesel,
            sex = applicant.sex,
            nationality = applicant.nationality?.let {
                citizenshipRepository.getReferenceById(it)
            },
            organizationalUnit = organizationalUnitRepository.getReferenceById(defaultStudentOrganizationalUnitString),
            middleSchool = applicant.highSchoolUsosCode?.let {
                schoolRepository.getReferenceById(it)
            },
            idNumber = applicant.primaryIdentityDocument?.number,
            documentType = applicant.primaryIdentityDocument?.number?.let {
                applicant.primaryIdentityDocument?.type
            },
            identityDocumentExpirationDate = applicant.primaryIdentityDocument?.expDate,
            identityDocumentIssuerCountry = applicant.primaryIdentityDocument?.country?.let {
                citizenshipRepository.getReferenceById(it)
            },
            mothersName = applicant.mothersName,
            fathersName = applicant.fathersName,
            wku = applicant.wku?.let {
                wkuRepository.getReferenceById(it)
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
                        schoolRepository.getReferenceById(schoolId)
                    }
                )
            }.forEach {
                addEntitlementDocument(it)
            }
            applicant.phoneNumbers.map {
                PhoneNumber(
                    number = it.number,
                    phoneNumberType = phoneNumberTypeRepository.getReferenceById(it.phoneNumberType),
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
                    documentType = documentTypeRepository.getReferenceById(it.baseOfStay ?: return@let),
                    person = this,
                    issueDate = it.polishCardIssueDate,
                    issueCountry = it.polishCardIssueCountry?.let { countryCode ->
                        citizenshipRepository.getReferenceById(countryCode)
                    },
                    number = it.polishCardNumber,
                    expirationDate = it.polishCardValidTo
                )
                addOwnedDocument(od)
            }
        }
        return personRepository.save(person)
    }

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    fun update(applicant: Applicant, person: Person): Person {
        val changeHistory = person.personChangeHistories.find { it.changeDate == Date() }
            ?: PersonChangeHistory(
                person = null,
                pesel = person.pesel,
                name = person.name,
                middleName = person.middleName,
                surname = person.surname,
                idNumber = person.idNumber,
                nip = person.nip,
                nationality = person.nationality,
                citizenship = person.citizenship,
                documentType = person.documentType,
                identityDocumentIssuerCountry = person.identityDocumentIssuerCountry,
                taxOffice = person.taxOffice,
                sex = person.sex
            )
        var changed: Boolean
        person.apply {
            logger.trace("Tworzę lub aktualizuję adresy")
            createOrUpdateAddresses(this, applicant)
            logger.trace("Tworzę lub aktualizuję numery telefonów")
            createOrUpdatePhoneNumbers(this, applicant)
            logger.trace("Tworzę lub aktualizuję dokumenty tożsamości")
            changed = createOrUpdateIdentityDocument(this, applicant)
            logger.trace("Tworzę lub aktualizuję dokumenty uprawniające do podjęcia studiów")
            createOrUpdateEntitlementDocument(person, applicant)
            logger.trace("Tworzę lub aktualizuję dokumenty posiadane")
            createOrUpdateOwnedDocuments(person, applicant)
            logger.trace("Tworzę lub aktualizuję zdjęcie")
            createOrUpdatePersonPhoto(person, applicant)
            logger.trace("Tworzę lub aktualizuję dane osobowe")
            if (applicant.email.endsWith(universityEmailSuffix)) {
                email = applicant.email
            } else {
                privateEmail = applicant.email
            }
            if (name != applicant.given) {
                changed = true
                name = applicant.given
            }
            if (middleName != applicant.middle && applicant.middle != null) {
                changed = true
                middleName = applicant.middle
            }
            if (surname != applicant.family) {
                changed = true
                surname = applicant.family
            }
            if (citizenship?.code != applicant.citizenship) {
                changed = true
                citizenship = applicant.citizenship?.let {
                    citizenshipRepository.getReferenceById(it)
                }
            }
            applicant.dateOfBirth?.let {
                birthDate = applicant.dateOfBirth
            }
            applicant.cityOfBirth?.let {
                birthCity = applicant.cityOfBirth
            }
            applicant.countryOfBirth?.let {
                birthCountry = citizenshipRepository.getReferenceById(it)
            }
            if (sex != applicant.sex) {
                changed = true
                sex = applicant.sex
            }
            if (nationality?.code != applicant.nationality && applicant.nationality != null) {
                changed = true
                nationality = citizenshipRepository.getReferenceById(applicant.nationality!!)
            }
            applicant.highSchoolUsosCode?.let {
                middleSchool = schoolRepository.getReferenceById(it)
            }
            applicant.mothersName?.let {
                mothersName = applicant.mothersName
            }
            applicant.fathersName?.let {
                fathersName = applicant.fathersName
            }
            applicant.wku?.let {
                wku = wkuRepository.getReferenceById(it)
            }
            applicant.militaryCategory?.let {
                militaryCategory = applicant.militaryCategory
            }
            applicant.militaryStatus?.let {
                militaryStatus = applicant.militaryStatus
            }
            if (changed) {
                person.addPersonChangeHistory(changeHistory)
            }
            if (externalDataStatus == 'O') {
                externalDataStatus = 'U'
            }
        }
        return person
    }

    private fun createOrUpdateIdentityDocument(
        person: Person,
        applicant: Applicant
    ): Boolean {
        var changed = false
        applicant.pesel?.let {
            if (person.pesel != it) {
                changed = true
                person.pesel = it
            }
        }
        var identityDocument = applicant.identityDocuments.find {
            person.idNumber == it.number
        }
        if (identityDocument == null && applicant.identityDocuments.size > 0) {
            identityDocument = applicant.primaryIdentityDocument
        }
        identityDocument?.number?.let {
            if (person.documentType != identityDocument.type) {
                changed = true
                person.documentType = identityDocument.type
            }
            if (person.idNumber != it) {
                changed = true
                person.idNumber = it
            }
            person.documentType = identityDocument.type
            person.identityDocumentExpirationDate = identityDocument.expDate
            person.identityDocumentIssuerCountry = identityDocument.country?.let { documentCountry ->
                citizenshipRepository.getReferenceById(documentCountry)
            }
        }
        return changed
    }

    private fun createOrUpdateAddresses(person: Person, applicant: Applicant) {
        applicant.addresses.forEach {
            createOrUpdateAddress(
                person = person,
                addressTypeCode = it.addressType.usosValue,
                city = it.city,
                street = it.street,
                houseNumber = it.streetNumber,
                apartmentNumber = it.flatNumber,
                zipCode = it.postalCode,
                cityIsCity = it.cityIsCity,
                countryCode = it.countryCode
            )
        }
        if (person.addresses.none { it.addressType.code == "KOR" }) {
            val foundAddress = person.addresses.find { it.addressType.code == "POB" }
                ?: person.addresses.find { it.addressType.code == "STA" }
            foundAddress?.let {
                createOrUpdateAddress(
                    person = person,
                    addressTypeCode = "KOR",
                    city = it.city,
                    street = it.street,
                    houseNumber = it.houseNumber,
                    apartmentNumber = it.flatNumber,
                    zipCode = it.zipCode,
                    cityIsCity = it.cityIsCity,
                    countryCode = it.country?.code
                )
            }
        }
    }

    private fun createOrUpdateAddress(
        person: Person,
        addressTypeCode: String,
        city: String?,
        street: String?,
        houseNumber: String?,
        apartmentNumber: String?,
        zipCode: String?,
        cityIsCity: Boolean?,
        countryCode: String?
    ) {
        val address = person.addresses.find { it.addressType.code == addressTypeCode }
        if (address != null) {
            addressService.update(
                address = address,
                city = city,
                street = street,
                houseNumber = houseNumber,
                apartmentNumber = apartmentNumber,
                zipCode = zipCode,
                cityIsCity = cityIsCity,
                countryCode = countryCode
            )
        } else {
            person.addAddress(
                addressService.create(
                    person = person,
                    addressTypeCode = addressTypeCode,
                    city = city,
                    street = street,
                    houseNumber = houseNumber,
                    apartmentNumber = apartmentNumber,
                    zipCode = zipCode,
                    cityIsCity = cityIsCity,
                    countryCode = countryCode
                )
            )
        }
    }

    private fun createOrUpdatePhoneNumbers(person: Person, applicant: Applicant) {
        applicant.phoneNumbers.forEach { phoneNumber ->
            val personPhoneNumber = person.phoneNumbers.find {
                phoneNumber.phoneNumberType == it.phoneNumberType.code
            }
            if (personPhoneNumber != null) {
                personPhoneNumber.phoneNumberType =
                    phoneNumberTypeRepository.getReferenceById(phoneNumber.phoneNumberType)
                personPhoneNumber.comments = phoneNumber.comment
            } else {
                person.addPhoneNumber(
                    PhoneNumber(
                        person = person,
                        phoneNumberType = phoneNumberTypeRepository.getReferenceById(phoneNumber.phoneNumberType),
                        number = phoneNumber.number,
                        comments = phoneNumber.comment
                    )
                )
            }
        }
    }

    private fun createOrUpdateEntitlementDocument(person: Person, applicant: Applicant) {
        applicant.documents.filter {
            it.certificateUsosCode != null
        }.filterNot {
            person.entitlementDocuments.any { entitlementDocument ->
                it.certificateUsosCode == entitlementDocument.type
            }
        }.forEach {
            val certificateUsosCode = it.certificateUsosCode ?: '?'
            person.addEntitlementDocument(
                EntitlementDocument(
                    person = person,
                    issueDate = it.issueDate,
                    description = it.issueInstitution.takeIf { _ ->
                        it.issueInstitutionUsosCode == null
                    },
                    number = it.documentNumber,
                    type = certificateUsosCode,
                    school = it.issueInstitutionUsosCode?.let { schoolId ->
                        schoolRepository.getReferenceById(schoolId)
                    }
                )
            )
        }
    }

    private fun createOrUpdatePersonPhoto(person: Person, applicant: Applicant) {
        logger.trace("Czekam na zdjęcie")
        applicant.photoByteArrayFuture?.get()?.let { photoByteArray ->
            logger.trace("Pobrałem zdjęcie. Sprawdzam czy osoba ma zdjęcie.")
            if (person.personPhoto != null) {
                logger.trace("Osoba ma zdjęcie. Aktualizuję...")
                person.personPhoto?.photoBlob = photoByteArray.toSerialBlob()
            } else {
                logger.trace("Osoba nie ma zdjęcia. Tworzę...")
                person.personPhoto = PersonPhoto(
                    person = person,
                    photoBlob = photoByteArray.toSerialBlob()
                )
            }
            logger.trace("Zakończyłem przetwarzanie zdjęcia.")
            val personPreference = person.personPreferences.find {
                it.attribute == "photo_visibility"
            }
            if (personPreference != null) {
                personPreference.value = applicant.photoPermission
            } else {
                person.addPersonPreference(
                    PersonPreference(person, "photo_visibility", applicant.photoPermission)
                )
            }
        }
    }

    private fun createOrUpdateOwnedDocuments(person: Person, applicant: Applicant) {
        when (applicant.applicantForeignerData?.baseOfStay) {
            "OKP" -> createOrUpdateOkp(person, applicant)
        }

    }

    private fun createOrUpdateOkp(person: Person, applicant: Applicant) {
        val afd = applicant.applicantForeignerData ?: throw IllegalArgumentException("Applicant foreigner data is null")
        val bof = afd.baseOfStay ?: throw IllegalArgumentException("Base of stay is null")
        val ownedDocument = ownedDocumentRepository.findByPersonAndDocumentType(
            person,
            documentTypeRepository.getReferenceById(bof)
        )
        if (ownedDocument != null) {
            updateOkp(ownedDocument, bof, afd)
        } else {
            createOkp(person, afd)
        }
    }

    private fun createOkp(
        person: Person,
        it: ApplicantForeignerData
    ) {
        val baseOfStay = it.baseOfStay ?: return
        person.addOwnedDocument(
            OwnedDocument(
                documentType = documentTypeRepository.getReferenceById(baseOfStay),
                person = person,
                issueDate = it.polishCardIssueDate,
                issueCountry = it.polishCardIssueCountry?.let { countryCode ->
                    citizenshipRepository.getReferenceById(countryCode)
                },
                number = it.polishCardNumber,
                expirationDate = it.polishCardValidTo
            )
        )
    }

    private fun updateOkp(
        ownedDocument: OwnedDocument,
        bof: String,
        afd: ApplicantForeignerData
    ) = ownedDocument.apply {
        documentType = documentTypeRepository.getReferenceById(bof)
        issueDate = afd.polishCardIssueDate
        issueCountry = afd.polishCardIssueCountry?.let { countryCode ->
            citizenshipRepository.getReferenceById(countryCode)
        }
        number = afd.polishCardNumber
        expirationDate = afd.polishCardValidTo
    }

    fun findOneByPeselOrIdNumberOrEmailOrPrivateEmail(
        personId: Long?,
        pesel: String,
        idNumbers: List<String>,
        email: String,
        privateEmail: String
    ): Person? {
        return personRepository.findOneByPeselOrIdNumberOrEmailOrPrivateEmail(
            personId,
            pesel,
            idNumbers,
            email,
            privateEmail
        )
    }

    fun createOrUpdatePerson(applicant: Applicant, person: Person?): Person {
        return if (person != null) {
            logger.trace("Osoba istnieje. Aktualizuję")
            applicant.personExisted = true
            update(applicant, person)
        } else {
            logger.trace("Osoba nie istnieje. Tworzę")
            create(applicant)
        }
    }

    fun findPotentialDuplicate(
        name: String,
        surname: String,
        birthDate: Date,
        email: String,
        privateEmail: String,
        idNumbers: List<String>
    ): List<PersonBasicData> {
        return personRepository.findPotentialDuplicate(name, surname, birthDate, email, privateEmail, idNumbers)
    }
}
