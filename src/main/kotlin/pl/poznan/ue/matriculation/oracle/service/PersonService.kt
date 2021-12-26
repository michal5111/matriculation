package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.configuration.LogExecutionTime
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.kotlinExtensions.toSerialBlob
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.ApplicantForeignerData
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.service.ApplicantService
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.entityRepresentations.PersonBasicData
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
    private val phoneNumberTypeRepository: PhoneNumberTypeRepository,
    private val applicantService: ApplicantService,
    private val addressService: AddressService,
    private val documentTypeRepository: DocumentTypeRepository,
    private val ownedDocumentRepository: OwnedDocumentRepository,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val didacticCycleRepository: DidacticCycleRepository,
    private val erasmusService: ErasmusService
) {
    private val logger: Logger = LoggerFactory.getLogger(PersonService::class.java)

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
        var changed = false
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
            if (name != applicant.name.given) {
                changed = true
                name = applicant.name.given
            }
            if (middleName != applicant.name.middle && applicant.name.middle != null) {
                changed = true
                middleName = applicant.name.middle
            }
            if (surname != applicant.name.family) {
                changed = true
                surname = applicant.name.family
            }
            if (citizenship?.code != applicant.citizenship) {
                changed = true
                citizenship = citizenshipRepository.getById(applicant.citizenship)
            }
            applicant.basicData.dateOfBirth?.let {
                birthDate = applicant.basicData.dateOfBirth
            }
            applicant.basicData.cityOfBirth?.let {
                birthCity = applicant.basicData.cityOfBirth
            }
            applicant.basicData.countryOfBirth?.let {
                birthCountry = citizenshipRepository.getById(it)
            }
            if (sex != applicant.basicData.sex) {
                changed = true
                sex = applicant.basicData.sex
            }
            if (nationality?.code != applicant.nationality && applicant.nationality != null) {
                changed = true
                nationality = citizenshipRepository.getById(applicant.nationality!!)
            }
            applicant.educationData.highSchoolUsosCode?.let {
                middleSchool = schoolRepository.getById(it)
            }

            applicant.additionalData.mothersName?.let {
                mothersName = applicant.additionalData.mothersName
            }
            applicant.additionalData.fathersName?.let {
                fathersName = applicant.additionalData.fathersName
            }
            applicant.additionalData.wku?.let {
                wku = wkuRepository.getById(it)
            }
            applicant.additionalData.militaryCategory?.let {
                militaryCategory = applicant.additionalData.militaryCategory
            }
            applicant.additionalData.militaryStatus?.let {
                militaryStatus = applicant.additionalData.militaryStatus
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
        applicant.basicData.pesel?.let {
            if (person.pesel != it) {
                changed = true
                person.pesel = it
            }
        }
        var identityDocument = applicant.identityDocuments.find {
            person.idNumber == it.number
        }
        if (identityDocument == null && applicant.identityDocuments.size > 0) {
            identityDocument = applicant.identityDocuments.first()
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
                citizenshipRepository.getById(documentCountry)
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
            person.addresses.find { it.addressType.code == "POB" }
                ?: person.addresses.find { it.addressType.code == "STA" }?.let {
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
                phoneNumber.number == it.number && phoneNumber.phoneNumberType == it.phoneNumberType.code
            }
            if (personPhoneNumber != null) {
                personPhoneNumber.phoneNumberType = phoneNumberTypeRepository.getById(phoneNumber.phoneNumberType)
                personPhoneNumber.comments = phoneNumber.comment
            } else {
                person.addPhoneNumber(
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
                        schoolRepository.getById(schoolId)
                    }
                )
            )
        }
    }

    private fun createOrUpdatePersonPhoto(person: Person, applicant: Applicant) =
        applicant.photoByteArrayFuture?.get()?.let { photoByteArray ->
            if (person.personPhoto != null) {
                person.personPhoto?.photoBlob = photoByteArray.toSerialBlob()
            } else {
                person.personPhoto = PersonPhoto(
                    person = person,
                    photoBlob = photoByteArray.toSerialBlob()
                )
            }
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
    ) {
        val baseOfStay = it.baseOfStay ?: return
        person.addOwnedDocument(
            OwnedDocument(
                documentType = documentTypeRepository.getById(baseOfStay),
                person = person,
                issueDate = it.polishCardIssueDate,
                issueCountry = it.polishCardIssueCountry?.let { countryCode ->
                    citizenshipRepository.getById(countryCode)
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
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    fun immatriculate(
        person: Person,
        importDto: Import,
        irkApplication: IrkApplication,
        application: Application
    ): Student {
        val student = studentService.createOrFindStudent(person, importDto.indexPoolCode)
        val isDefaultProgramme = if (person.personProgrammes.any { it.isDefault == true }) {
            personProgrammeRepository.updateToNotDefault(importDto.dateOfAddmision) == 1
        } else true
        val personProgramme = studentService.createPersonProgramme(
            person = person,
            importDto = importDto,
            student = student,
            certificate = application.certificate,
            sourceOfFinancing = application.applicationForeignerData?.sourceOfFinancing,
            basisOfAdmission = application.applicationForeignerData?.basisOfAdmission,
            isDefault = isDefaultProgramme
        )
        student.addPersonProgramme(personProgramme)
        irkApplication.personProgramme = personProgramme
        personProgramme.irkApplication = irkApplication
        addPersonArrival(person, application, importDto.didacticCycleCode, personProgramme, importDto.startDate)
        personProgrammeRepository.save(personProgramme)
        return student
    }

    private fun addPersonArrival(
        person: Person,
        application: Application,
        didacticCycleCode: String,
        personProgramme: PersonProgramme,
        startDate: Date
    ) = application.applicant?.erasmusData?.let {
        val didacticCycle = didacticCycleRepository.getById(didacticCycleCode)
        val didacticCycleYear = didacticCycleRepository.findDidacticCycleYearBySemesterDates(
            didacticCycle.dateFrom,
            didacticCycle.dateTo
        ) ?: throw EntityNotFoundException("Nie można znaleźć cyklu dydaktycznego")
        val arrival = erasmusService.createArrival(
            erasmusData = it,
            didacticCycle = didacticCycle,
            didacticCycleYear = didacticCycleYear,
            didacticCycleCode = didacticCycleCode,
            startDate = startDate
        )
        personProgramme.addArrival(arrival)
        person.addPersonArrivals(arrival)
        arrival
    }

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    fun process(
        application: Application, importDto: Import, postMatriculation: (applicationId: Long) -> Int
    ): Pair<Person, Student> {
        logger.trace("Tworzę lub aktualizuję osobę")
        val applicant = application.applicant ?: throw ApplicantNotFoundException()
        val person: Person = createOrUpdatePerson(applicant)
        logger.trace("Tworzę potwierdzenie immatrykulacji")
        val dataSourceId = application.dataSourceId
        val irkApplication = IrkApplication(
            applicationId = application.foreignId,
            confirmationStatus = 0,
            irkInstance = dataSourceId?.let {
                applicationDataSourceFactory.getDataSource(dataSourceId).instanceUrl
            } ?: "Nieznany"
        )
        logger.trace("Tworzę lub wybieram studenta")
        val student = immatriculate(
            person = person,
            importDto = importDto,
            irkApplication = irkApplication,
            application = application
        )
        logger.trace("Wykonuję operacje poimmatrykulacyjne")
        irkApplication.confirmationStatus = postMatriculation(application.foreignId)
        return Pair(person, student)
    }

    private fun createOrUpdatePerson(applicant: Applicant): Person {
        logger.trace("Szukam osoby w bazie")
        var person = personRepository.findOneByPeselOrIdNumberOrEmailOrPrivateEmail(
            applicant.usosId,
            applicant.basicData.pesel.orEmpty(),
            applicant.identityDocuments.filterNot {
                it.number.isNullOrBlank()
            }.map {
                it.number?.uppercase(Locale.getDefault()).orEmpty()
            },
            applicant.email.uppercase(),
            applicant.email.uppercase()
        )

        person = if (person != null) {
            logger.trace("Osoba istnieje. Aktualizuję")
            update(applicant, person)
        } else {
            logger.trace("Osoba nie istnieje. Tworzę")
            create(applicant)
        }
        return person
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
