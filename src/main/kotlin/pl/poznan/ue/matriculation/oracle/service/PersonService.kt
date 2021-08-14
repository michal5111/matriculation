package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.configuration.LogExecutionTime
import pl.poznan.ue.matriculation.kotlinExtensions.nameCapitalize
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.ApplicantForeignerData
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.dto.ImportDtoJpa
import pl.poznan.ue.matriculation.local.service.ApplicantService
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.repo.*
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class PersonService(
    private val personRepository: PersonRepository,
    private val studentService: StudentService,
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
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val didacticCycleRepository: DidacticCycleRepository,
    private val erasmusService: ErasmusService
) {
    val logger: Logger = LoggerFactory.getLogger(PersonService::class.java)

    @Autowired
    private lateinit var _self: PersonService

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitString: String

    @Value("\${pl.poznan.ue.matriculation.universityEmailSuffix}")
    lateinit var universityEmailSuffix: String

    @LogExecutionTime
    fun create(applicant: Applicant): Person {
        val person = applicantService.createPersonFromApplicant(applicant)
        return personRepository.save(person)
    }

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.MANDATORY,
        transactionManager = "oracleTransactionManager"
    )
    fun update(applicant: Applicant, person: Person): Person {
        val changeHistory = personChangeHistoryRepository.findByPersonIdAndChangeDate(person.id, Date())
            ?: PersonChangeHistory(person = person)
        person.apply {
            createOrUpdateAddresses(this, applicant)
            createOrUpdatePhoneNumbers(this, applicant)
            createOrUpdateIdentityDocument(this, applicant, changeHistory)
            createOrUpdateEntitlementDocument(person, applicant)
            createOrUpdatePersonPhoto(person, applicant)
            createOrUpdateOwnedDocuments(person, applicant)
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
                citizenship = citizenshipRepository.getById(applicant.citizenship)
            }
            applicant.basicData.dateOfBirth?.let {
                birthDate = applicant.basicData.dateOfBirth
            }
            applicant.basicData.cityOfBirth?.let {
                birthCity = applicant.basicData.cityOfBirth
            }
            birthCountry = applicant.basicData.countryOfBirth?.let {
                citizenshipRepository.getById(it)
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
                nationality = citizenshipRepository.getById(applicant.nationality!!)
            }
            organizationalUnit = organizationalUnitRepository.getById(defaultStudentOrganizationalUnitString)
            middleSchool = applicant.educationData.highSchoolUsosCode?.let {
                schoolRepository.getById(it)
            }

            applicant.additionalData.mothersName?.let {
                mothersName = applicant.additionalData.mothersName
            }
            applicant.additionalData.fathersName?.let {
                fathersName = applicant.additionalData.fathersName
            }
            wku = applicant.additionalData.wku?.let {
                wkuRepository.getById(it)
            }
            applicant.additionalData.militaryCategory?.let {
                militaryCategory = applicant.additionalData.militaryCategory
            }
            applicant.additionalData.militaryStatus?.let {
                militaryStatus = applicant.additionalData.militaryStatus
            }
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
        if (person.externalDataStatus == 'O') {
            person.externalDataStatus = 'U'
        }
        return person
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
                citizenshipRepository.getById(documentCountry)
            }
        }
    }

    private fun createOrUpdateAddresses(person: Person, applicant: Applicant) {
        applicant.addresses.forEach {
            createOrUpdateAddress(
                person = person,
                addressType = addressTypeRepository.getById(it.addressType.usosValue),
                city = it.city,
                street = it.street,
                houseNumber = it.streetNumber,
                apartmentNumber = it.flatNumber,
                zipCode = it.postalCode,
                cityIsCity = it.cityIsCity,
                countryCode = it.countryCode
            )
        }
        val addressType = addressTypeRepository.getById("KOR")
        if (!addressRepository.existsByPersonIdAndAddressType(person.id, addressType)) {
            addressRepository.findByPersonIdAndAddressType(person.id, addressTypeRepository.getById("STA"))?.let {
                createOrUpdateAddress(
                    person = person,
                    addressType = addressType,
                    city = it.city,
                    street = it.street,
                    houseNumber = it.houseNumber,
                    apartmentNumber = it.apartmentNumber,
                    zipCode = it.zipCode,
                    cityIsCity = it.cityIsCity == 'T',
                    countryCode = it.countryCode?.code
                )
            }
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
        val address = addressRepository.findByPersonIdAndAddressType(person.id, addressType)
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
            val personPhoneNumber = phoneNumberRepository.findByPersonIdAndNumber(person.id, phoneNumber.number)
            if (personPhoneNumber != null) {
                personPhoneNumber.phoneNumberType = phoneNumberTypeRepository.getById(phoneNumber.phoneNumberType)
                personPhoneNumber.comments = phoneNumber.comment
            } else {
                person.phoneNumbers.add(
                    PhoneNumber(
                        person = person,
                        phoneNumberType = phoneNumberTypeRepository.getById(phoneNumber.phoneNumberType),
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
        }.filterNot {
            entitlementDocumentRepository.existsByPersonIdAndType(person.id, it.certificateUsosCode!!)
        }.forEach {
            person.entitlementDocuments.add(
                EntitlementDocument(
                    person = person,
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
            )
        }
    }

    private fun createOrUpdatePersonPhoto(person: Person, applicant: Applicant) = applicant.photoByteArray?.let {
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

    private fun createOrUpdateOwnedDocuments(person: Person, applicant: Applicant) {
        when {
            applicant.applicantForeignerData?.baseOfStay != null
                    && applicant.applicantForeignerData?.baseOfStay == "OKP" -> createOrUpdateOkp(person, applicant)
        }

    }

    private fun createOrUpdateOkp(person: Person, applicant: Applicant) {
        val afd = applicant.applicantForeignerData ?: throw IllegalArgumentException("Applicant foreigner data is null")
        val bof = afd.baseOfStay ?: throw IllegalArgumentException("Base of stay is null")
        val ownedDocument = ownedDocumentRepository.findByPersonAndDocumentType(
            person,
            documentTypeRepository.getById(bof)
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
    ) = person.ownedDocuments.add(
        OwnedDocument(
            documentType = documentTypeRepository.getById(it.baseOfStay!!),
            person = person,
            issueDate = it.polishCardIssueDate,
            issueCountry = it.polishCardIssueCountry?.let { countryCode ->
                citizenshipRepository.getById(countryCode)
            },
            number = it.polishCardNumber,
            expirationDate = it.polishCardValidTo
        )
    )

    private fun updateOkp(
        ownedDocument: OwnedDocument,
        bof: String,
        afd: ApplicantForeignerData
    ) = ownedDocument.apply {
        documentType = documentTypeRepository.getById(bof)
        issueDate = afd.polishCardIssueDate
        issueCountry = afd.polishCardIssueCountry?.let { countryCode ->
            citizenshipRepository.getById(countryCode)
        }
        number = afd.polishCardNumber
        expirationDate = afd.polishCardValidTo
    }

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, java.lang.RuntimeException::class],
        propagation = Propagation.MANDATORY,
        transactionManager = "oracleTransactionManager"
    )
    fun immatriculate(
        person: Person,
        importDto: ImportDtoJpa,
        irkApplication: IrkApplication,
        application: Application
    ): Student {
        val student = studentService.createOrFindStudent(person, importDto.indexPoolCode)
        logger.info("Wykonuję getDefaultPersonProgramme")
        val startTime = System.nanoTime()
        val isDefaultProgramme = personProgrammeRepository.getDefaultProgramme(person.id)?.let {
            if (it.plannedDateOfCompletion == null || it.plannedDateOfCompletion!! <= importDto.dateOfAddmision || it.status != "STU") {
                logger.info("Zmieniam na nie default")
                //it.isDefault = 'N'
                //personProgrammeRepository.saveAndFlush(it)
                personProgrammeRepository.updateToNotDefault(it.id)
                return@let 'N'
            }
            //it.isDefault
            return@let 'T'
        }
        val stopTime = System.nanoTime()
        val time = (stopTime - startTime) / 1000000
        logger.info("Zakończyłem wykonywanie getDefaultPersonProgramme Time: $time ms")
        val personProgramme = studentService.createPersonProgramme(
            person = person,
            importDto = importDto,
            student = student,
            certificate = application.certificate,
            sourceOfFinancing = application.applicationForeignerData?.sourceOfFinancing,
            basisOfAdmission = application.applicationForeignerData?.basisOfAdmission,
            isDefault = isDefaultProgramme != 'T'
        )
        irkApplication.personProgramme = personProgramme
        personProgramme.irkApplication = irkApplication
        addPersonArrival(application, importDto.didacticCycleCode, person, personProgramme, importDto.startDate)
        personProgrammeRepository.save(personProgramme)
        return student
    }

    private fun addPersonArrival(
        application: Application,
        didacticCycleCode: String,
        person: Person,
        personProgramme: PersonProgramme,
        startDate: Date
    ) = application.applicant?.erasmusData?.let {
        val didacticCycle = didacticCycleRepository.getById(didacticCycleCode)
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

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    fun process(
        application: Application, importDto: ImportDtoJpa, postMatriculation: (applicationId: Long) -> Int
    ): Pair<Person, Student> {
        val person: Person = createOrUpdatePerson(application.applicant!!)
        val irkApplication = IrkApplication(
            applicationId = application.foreignId,
            confirmationStatus = 0,
            irkInstance = applicationDataSourceFactory.getDataSource(application.dataSourceId!!).instanceUrl
        )
        val student = _self.immatriculate(
            person = person,
            importDto = importDto,
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
            person = applicant.identityDocuments.filterNot {
                it.number.isNullOrBlank()
            }.map {
                it.number!!.trim().uppercase(Locale.getDefault())
            }.let {
                personRepository.findByIdNumberIn(it)
            }
        }
        if (person == null) {
            person = personRepository.findOneByEmail(applicant.email)
        }
        if (person == null) {
            person = personRepository.findOneByPrivateEmail(applicant.email)
        }
        logger.info("Person1: {} {}", person?.modificationDate, person)
        if (person != null) {
            person = _self.update(applicant, person!!)
        } else {
            person = _self.create(applicant)
        }
        logger.info("Person2: {} {}", person?.modificationDate, person)
        return person!!
    }
}