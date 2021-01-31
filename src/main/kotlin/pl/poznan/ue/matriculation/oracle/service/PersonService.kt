package pl.poznan.ue.matriculation.oracle.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.kotlinExtensions.nameCapitalize
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.service.ApplicantService
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceService
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.repo.*
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class PersonService(
    private val personRepository: PersonRepository,
    private val studentService: StudentService,
    private val studentRepository: StudentRepository,
    private val personProgrammeRepository: PersonProgrammeRepository,
    private val organizationalUnitRepository: OrganizationalUnitRepository,
    private val citizenshipRepository: CitizenshipRepository,
    private val schoolRepository: SchoolRepository,
    private val wkuRepository: WkuRepository,
    private val addressTypeRepository: AddressTypeRepository,
    private val addressRepository: AddressRepository,
    private val phoneNumberTypeRepository: PhoneNumberTypeRepository,
    private val phoneNumberRepository: PhoneNumberRepository,
    private val entitlementDocumentRepository: EntitlementDocumentRepository,
    private val applicantService: ApplicantService,
    private val addressService: AddressService,
    private val irkApplicationRepository: IrkApplicationRepository,
    private val personPreferenceRepository: PersonPreferenceRepository,
    private val documentTypeRepository: DocumentTypeRepository,
    private val ownedDocumentRepository: OwnedDocumentRepository,
    private val personChangeHistoryRepository: PersonChangeHistoryRepository,
    private val applicationDataSourceService: ApplicationDataSourceService,
    private val didacticCycleRepository: DidacticCycleRepository,
    private val erasmusService: ErasmusService
) {

    @Autowired
    private lateinit var _self: PersonService

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitString: String

    @Value("\${pl.poznan.ue.matriculation.universityEmailSuffix}")
    lateinit var universityEmailSuffix: String

    fun create(applicant: Applicant): Person {
        return applicantService.createPersonFromApplicant(applicant)
    }

    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.MANDATORY,
        transactionManager = "oracleTransactionManager"
    )
    fun update(applicant: Applicant, person: Person) {
        val changeHistory = personChangeHistoryRepository.findByPersonAndChangeDate(person, Date())
            ?: PersonChangeHistory(person = person)
        person.apply {
            if (applicant.email.endsWith(universityEmailSuffix)) {
                email = applicant.email
            } else {
                privateEmail = applicant.email
            }
            if (name != applicant.name.given) {
                changeHistory.name = name
                name = applicant.name.given.nameCapitalize()
            }
            if (middleName != applicant.name.middle) {
                changeHistory.middleName = middleName
                middleName = applicant.name.middle?.nameCapitalize()
            }
            if (surname != applicant.name.family) {
                changeHistory.surname = surname
                surname = applicant.name.family.nameCapitalize()
            }
            if (citizenship?.code != applicant.citizenship) {
                changeHistory.citizenship = citizenship
                citizenship = citizenshipRepository.getOne(applicant.citizenship)
            }
            applicant.basicData.dateOfBirth?.let {
                birthDate = applicant.basicData.dateOfBirth
            }
            applicant.basicData.cityOfBirth?.let {
                birthCity = applicant.basicData.cityOfBirth
            }
            birthCountry = applicant.basicData.countryOfBirth?.let {
                citizenshipRepository.getOne(it)
            }
            if (sex != applicant.basicData.sex) {
                changeHistory.sex = sex
                sex = applicant.basicData.sex
            }
            if (nationality?.code != applicant.nationality && applicant.nationality != null) {
                personChangeHistory.add(
                    PersonChangeHistory(
                        person = person,
                        nationality = nationality
                    )
                )
                nationality = citizenshipRepository.getOne(applicant.nationality!!)
            }
            organizationalUnit = organizationalUnitRepository.getOne(defaultStudentOrganizationalUnitString)
            middleSchool = applicant.educationData.highSchoolUsosCode?.let {
                schoolRepository.getOne(it)
            }
            createOrUpdateAddresses(this, applicant)
            createOrUpdatePhoneNumbers(this, applicant)
            createOrUpdateIdentityDocument(this, applicant, changeHistory)

            applicant.additionalData.mothersName?.let {
                mothersName = applicant.additionalData.mothersName
            }
            applicant.additionalData.fathersName?.let {
                fathersName = applicant.additionalData.fathersName
            }
            wku = applicant.additionalData.wku?.let {
                wkuRepository.getOne(it)
            }
            createOrUpdateEntitlementDocument(person, applicant)
            applicant.additionalData.militaryCategory?.let {
                militaryCategory = applicant.additionalData.militaryCategory
            }
            applicant.additionalData.militaryStatus?.let {
                militaryStatus = applicant.additionalData.militaryStatus
            }
            createOrUpdatePersonPhoto(person, applicant)
            createOrUpdateOwnedDocuments(person, applicant)
            if (
                changeHistory.name != null
                || changeHistory.middleName != null
                || changeHistory.surname != null
                || changeHistory.citizenship != null
                || changeHistory.idNumber != null
                || changeHistory.documentType != null
                || changeHistory.pesel != null
                || changeHistory.sex != null
            ) {
                person.personChangeHistory.add(changeHistory)
            }
        }
    }

    private fun createOrUpdateIdentityDocument(
        person: Person,
        applicant: Applicant,
        changeHistory: PersonChangeHistory
    ) {
        applicant.basicData.pesel?.let {
            if (person.pesel != it) {
                changeHistory.pesel = person.pesel
                person.pesel = it
            }
        }
        var identityDocument = applicant.identityDocuments.find {
            person.idNumber?.replace(" ", "") == it.number
        }
        if (identityDocument == null && applicant.identityDocuments.size > 0) {
            identityDocument = applicant.identityDocuments[0]
        }
        identityDocument?.number?.let {
            if (person.documentType != identityDocument.type) {
                changeHistory.documentType = person.documentType
                person.documentType = identityDocument.type
            }
            if (person.idNumber != it) {
                changeHistory.idNumber = person.idNumber
                person.idNumber = it
            }
            person.documentType = identityDocument.type
            person.identityDocumentExpirationDate = identityDocument.expDate
            person.identityDocumentIssuerCountry = identityDocument.country?.let { documentCountry ->
                citizenshipRepository.getOne(documentCountry)
            }
        }
    }

    private fun createOrUpdateAddresses(person: Person, applicant: Applicant) {
        applicant.addresses.forEach {
            createOrUpdateAddress(
                person = person,
                addressType = addressTypeRepository.getOne(it.addressType.usosValue),
                city = it.city,
                street = it.street,
                houseNumber = it.streetNumber,
                apartmentNumber = it.flatNumber,
                zipCode = it.postalCode,
                cityIsCity = it.cityIsCity,
                countryCode = it.countryCode
            )
        }
    }

    private fun createOrUpdateAddress(
        person: Person,
        addressType: AddressType,
        city: String?,
        street: String?,
        houseNumber: String?,
        apartmentNumber: String?,
        zipCode: String?,
        cityIsCity: Boolean,
        countryCode: String?
    ) {
        val address = addressRepository.findByPersonAndAddressType(person, addressType)
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
            person.addresses.add(
                addressService.create(
                    person = person,
                    addressType = addressType,
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
            val personPhoneNumber = phoneNumberRepository.findByPersonAndNumber(person, phoneNumber.number)
            if (personPhoneNumber != null) {
                personPhoneNumber.phoneNumberType = phoneNumberTypeRepository.getOne(phoneNumber.phoneNumberType)
                personPhoneNumber.comments = phoneNumber.comment
            } else {
                person.phoneNumbers.add(
                    PhoneNumber(
                        person = person,
                        phoneNumberType = phoneNumberTypeRepository.getOne(phoneNumber.phoneNumberType),
                        number = phoneNumber.number,
                        comments = phoneNumber.comment
                    )
                )
            }
        }
    }

    private fun createOrUpdateEntitlementDocument(person: Person, applicant: Applicant) {
        applicant.educationData.documents.filter {
            it.certificateUsosCode != null
        }.forEach {
            if (entitlementDocumentRepository.existsByPersonAndType(person, it.certificateUsosCode!!)) {
                return
            } else {
                person.entitlementDocuments.add(
                    EntitlementDocument(
                        person = person,
                        issueDate = it.issueDate,
                        description = it.issueInstitution.takeIf { _ ->
                            it.issueInstitutionUsosCode == null
                        },
                        number = it.documentNumber,
                        type = it.certificateUsosCode,
                        school = it.issueInstitutionUsosCode?.let { schoolId ->
                            schoolRepository.getOne(schoolId)
                        }
                    )
                )
            }
        }
    }

    private fun createOrUpdatePersonPhoto(person: Person, applicant: Applicant) {
        applicant.photoByteArray?.let {
            if (person.personPhoto != null) {
                person.personPhoto!!.photoBlob = it
            } else {
                person.personPhoto = PersonPhoto(
                    person = person,
                    photoBlob = it
                )
            }
            val personPreference = personPreferenceRepository.findByIdOrNull(
                PersonPreferenceId(
                    person.id!!, "photo_visibility"
                )
            )
            if (personPreference != null) {
                personPreference.value = applicant.photoPermission
            } else {
                person.personPreferences.add(
                    PersonPreference(person, "photo_visibility", applicant.photoPermission)
                )
            }
        }
    }

    private fun createOrUpdateOwnedDocuments(person: Person, applicant: Applicant) {
        if (applicant.applicantForeignerData?.baseOfStay == null
            || applicant.applicantForeignerData?.baseOfStay != "OKP"
        ) {
            return
        }
        val ownedDocument = ownedDocumentRepository.findByPersonAndDocumentType(
            person,
            documentTypeRepository.getOne(applicant.applicantForeignerData!!.baseOfStay!!)
        )
        if (ownedDocument != null) {
            ownedDocument.apply {
                documentType = documentTypeRepository.getOne(applicant.applicantForeignerData!!.baseOfStay!!)
                issueDate = applicant.applicantForeignerData!!.polishCardIssueDate
                issueCountry = applicant.applicantForeignerData!!.polishCardIssueCountry?.let { countryCode ->
                    citizenshipRepository.getOne(countryCode)
                }
                number = applicant.applicantForeignerData!!.polishCardNumber
                expirationDate = applicant.applicantForeignerData!!.polishCardValidTo
            }
        } else {
            applicant.applicantForeignerData.let {
                if (it!!.baseOfStay == null || it.baseOfStay != "OKP") {
                    return@let
                }
                person.ownedDocuments.add(
                    OwnedDocument(
                        documentType = documentTypeRepository.getOne(it.baseOfStay!!),
                        person = person,
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

    @Transactional(
        rollbackFor = [java.lang.Exception::class, java.lang.RuntimeException::class],
        propagation = Propagation.MANDATORY,
        transactionManager = "oracleTransactionManager"
    )
    fun immatriculate(
        person: Person,
        programmeCode: String,
        registration: String,
        indexPoolCode: String,
        startDate: Date,
        dateOfAddmision: Date,
        stageCode: String,
        didacticCycleCode: String,
        irkApplication: IrkApplication,
        application: Application
    ): Student {
        val student = studentService.createOrFindStudent(person, indexPoolCode)
        studentRepository.save(student)
        val isDefaultProgramme = personProgrammeRepository.getDefaultProgramme(person.id!!)?.let {
            if (it.plannedDateOfCompletion == null || it.plannedDateOfCompletion!! <= dateOfAddmision || it.status != "STU") {
                it.isDefault = 'N'
                personProgrammeRepository.saveAndFlush(it)
            }
            it.isDefault
        }
        val personProgramme = studentService.createPersonProgramme(
            person = person,
            programmeCode = programmeCode,
            startDate = startDate,
            dateOfAddmision = dateOfAddmision,
            didacticCycleCode = didacticCycleCode,
            stageCode = stageCode,
            student = student,
            certificate = application.certificate,
            sourceOfFinancing = application.applicationForeignerData?.sourceOfFinancing,
            basisOfAdmission = application.applicationForeignerData?.basisOfAdmission,
            isDefault = isDefaultProgramme != 'T'
        )
        irkApplication.personProgramme = personProgramme
        personProgramme.irkApplication = irkApplication
        application.applicant?.erasmusData?.let {
            val didacticCycle = didacticCycleRepository.getOne(didacticCycleCode)
            val didacticCycleYear = didacticCycleRepository.findDidacticCycleYearBySemesterDates(
                didacticCycle.dateFrom,
                didacticCycle.dateTo
            ) ?: throw EntityNotFoundException("Nie można znaleźć cyklu dydaktycznego")
            person.personArrivals.add(
                erasmusService.createArrival(
                    person = person,
                    erasmusData = it,
                    didacticCycle = didacticCycle,
                    didacticCycleYear = didacticCycleYear,
                    didacticCycleCode = didacticCycleCode,
                    personProgramme = personProgramme,
                    startDate = startDate
                )
            )
        }
        personProgrammeRepository.save(personProgramme)
        return student
    }

    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    fun process(
        application: Application,
        dateOfAddmision: Date,
        didacticCycleCode: String,
        indexPoolCode: String,
        programmeCode: String,
        registration: String,
        stageCode: String,
        startDate: Date,
        postMatriculation: (applicationId: Long) -> Int
    ): Pair<Person, Student> {
        val person: Person = createOrUpdatePerson(application.applicant!!)
        personRepository.save(person)
        val irkApplication = IrkApplication(
            applicationId = application.foreignId,
            confirmationStatus = 0,
            irkInstance = applicationDataSourceService.getDataSource(application.dataSourceId!!).instanceUrl
        )
        val student = _self.immatriculate(
            person = person,
            dateOfAddmision = dateOfAddmision,
            didacticCycleCode = didacticCycleCode,
            indexPoolCode = indexPoolCode,
            programmeCode = programmeCode,
            registration = registration,
            stageCode = stageCode,
            startDate = startDate,
            irkApplication = irkApplication,
            application = application
        )
        irkApplication.confirmationStatus = postMatriculation.invoke(application.foreignId)
        irkApplicationRepository.save(irkApplication)
        return Pair(person, student)
    }

    private fun createOrUpdatePerson(applicant: Applicant): Person {
        var person: Person? = null
        applicant.usosId?.let { usosId ->
            person = personRepository.findByIdOrNull(usosId)
        }
        if (person == null) {
            applicant.basicData.pesel?.let { pesel ->
                person = personRepository.findOneByPesel(pesel.trim())
            }
        }
        if (person == null) {
            applicant.identityDocuments.forEach {
                if (person != null) {
                    return@forEach
                }
                it.number?.let { idNumber ->
                    person = personRepository.findOneByIdNumber(idNumber)
                }
            }
        }
        if (person == null) {
            person = personRepository.findOneByEmail(applicant.email)
        }
        if (person == null) {
            person = personRepository.findOneByPrivateEmail(applicant.email)
        }
        if (person != null) {
            _self.update(applicant, person!!)
        } else {
            person = _self.create(applicant)
        }
        return person!!
    }
}