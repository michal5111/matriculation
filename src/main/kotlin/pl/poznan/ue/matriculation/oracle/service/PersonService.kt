package pl.poznan.ue.matriculation.oracle.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpStatusCodeException
import pl.poznan.ue.matriculation.irk.dto.ErrorMessageDto
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.service.ApplicantService
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.repo.*
import java.util.*

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
        private val irkService: IrkService,
        private val entitlementDocumentRepository: EntitlementDocumentRepository,
        private val applicantService: ApplicantService,
        private val addressService: AddressService,
        private val irkApplicationRepository: IrkApplicationRepository,
        private val personPreferenceRepository: PersonPreferenceRepository,
        private val documentTypeRepository: DocumentTypeRepository,
        private val ownedDocumentRepository: OwnedDocumentRepository,
        private val personChangeHistoryRepository: PersonChangeHistoryRepository
) {

    @Autowired
    private lateinit var _self: PersonService

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitString: String

    @Value("\${pl.poznan.ue.matriculation.universityEmailSuffix}")
    lateinit var universityEmailSuffix: String

    @Value("\${pl.poznan.ue.matriculation.setAsAccepted}")
    private var setAsAccepted: Boolean = false

    private var lastChangeDate: Date? = null

    fun create(applicant: Applicant): Person {
        return applicantService.createPersonFromApplicant(applicant)
    }

    @Transactional(rollbackFor = [java.lang.Exception::class, RuntimeException::class], propagation = Propagation.MANDATORY, transactionManager = "oracleTransactionManager")
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
                name = applicant.name.given.capitalize()
            }
            if (middleName != applicant.name.middle) {
                changeHistory.middleName = middleName
                middleName = applicant.name.middle?.capitalize()
            }
            if (surname != applicant.name.family) {
                changeHistory.surname = surname
                surname = applicant.name.family.capitalize()
            }
            if (citizenship?.code != applicant.citizenship) {
                changeHistory.citizenship = citizenship
                citizenship = citizenshipRepository.getOne(applicant.citizenship!!)
            }
            birthDate = applicant.basicData.dateOfBirth
            birthCity = applicant.basicData.cityOfBirth
            birthCountry = citizenshipRepository.getOne(applicant.basicData.countryOfBirth)
            if (sex != applicant.basicData.sex) {
                changeHistory.sex = sex
                sex = applicant.basicData.sex
            }
//            if (nationality?.code != applicant.basicData.countryOfBirth) {
//                personChangeHistory.add(PersonChangeHistory(
//                        person = person,
//                        nationality = nationality
//                ))
//                nationality = citizenshipRepository.getOne(applicant.basicData.countryOfBirth)
//            }
            organizationalUnit = organizationalUnitRepository.getOne(defaultStudentOrganizationalUnitString)
            middleSchool = applicant.educationData.highSchoolUsosCode?.let {
                schoolRepository.getOne(it)
            }
            createOrUpdateAddresses(this, applicant)
            createOrUpdatePhoneNumbers(this, applicant)
            createOrUpdateIdentityDocument(this, applicant, changeHistory)

            mothersName = applicant.additionalData.mothersName
            fathersName = applicant.additionalData.fathersName
            wku = applicant.additionalData.wku?.let {
                wkuRepository.getOne(it)
            }
            createOrUpdateEntitlementDocument(person, applicant)
            militaryCategory = applicant.additionalData.militaryCategory
            militaryStatus = applicant.additionalData.militaryStatus
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

    private fun createOrUpdateIdentityDocument(person: Person, applicant: Applicant, changeHistory: PersonChangeHistory) {
        applicant.basicData.pesel?.let {
            if (person.pesel != it) {
                changeHistory.pesel = person.pesel
                person.pesel = it
            }
        }
        applicant.additionalData.documentNumber?.let {
            if (person.documentType != applicant.additionalData.documentType) {
                changeHistory.documentType = person.documentType
                person.documentType = applicant.additionalData.documentType
            }
            if (person.idNumber != it) {
                changeHistory.idNumber = person.idNumber
                person.idNumber = it
            }
            person.documentType = applicant.additionalData.documentType
            person.identityDocumentExpirationDate = applicant.additionalData.documentExpDate
            person.identityDocumentIssuerCountry = applicant.additionalData.documentCountry?.let { documentCountry ->
                citizenshipRepository.getOne(documentCountry)
            }
        }
    }

    private fun createOrUpdateAddresses(person: Person, applicant: Applicant) {
        createOrUpdateAddress(
                person = person,
                addressType = addressTypeRepository.getOne("STA"),
                city = applicant.contactData.officialCity,
                street = applicant.contactData.officialStreet,
                houseNumber = applicant.contactData.officialStreetNumber,
                apartmentNumber = applicant.contactData.officialFlatNumber,
                zipCode = applicant.contactData.officialPostCode,
                cityIsCity = applicant.contactData.officialCityIsCity,
                countryCode = applicant.contactData.officialCountry
        )

        createOrUpdateAddress(
                person = person,
                addressType = addressTypeRepository.getOne("KOR"),
                city = applicant.contactData.realCity,
                street = applicant.contactData.realStreet,
                houseNumber = applicant.contactData.realStreetNumber,
                apartmentNumber = applicant.contactData.realFlatNumber,
                zipCode = applicant.contactData.realPostCode,
                cityIsCity = applicant.contactData.realCityIsCity,
                countryCode = applicant.contactData.realCountry
        )
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
        if (city.isNullOrBlank() || street.isNullOrBlank() || houseNumber.isNullOrBlank() || zipCode.isNullOrBlank()) {
            return
        }
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
        applicant.contactData.phoneNumber?.run {
            val primaryPhoneNumber = phoneNumberRepository.findByPersonAndNumber(person, this)
            primaryPhoneNumber?.apply {
                number = applicant.contactData.phoneNumber!!
                comments = "Podstawowy numer telefonu"
            }
                    ?: person.phoneNumbers.add(
                            PhoneNumber(
                                    person = person,
                                    phoneNumberType = phoneNumberTypeRepository.getOne(applicant.contactData.phoneNumberType!!),
                                    number = applicant.contactData.phoneNumber!!,
                                    comments = "Podstawowy numer telefonu"
                            )
                    )
        }
        applicant.contactData.phoneNumber2?.run {
            val secondaryPhoneNumber = phoneNumberRepository.findByPersonAndNumber(person, this)
            if (this == applicant.contactData.phoneNumber) {
                return
            }
            secondaryPhoneNumber?.apply {
                number = applicant.contactData.phoneNumber2!!
                comments = "Alternatywny numer telefonu"
            }
                    ?: person.phoneNumbers.add(
                            PhoneNumber(
                                    person = person,
                                    phoneNumberType = phoneNumberTypeRepository.getOne(applicant.contactData.phoneNumber2Type!!),
                                    number = applicant.contactData.phoneNumber2!!,
                                    comments = "Alternatywny numer telefonu"
                            )
                    )
        }
    }

    private fun createOrUpdateEntitlementDocument(person: Person, applicant: Applicant) {
        applicant.educationData.documents.filter {
            it.certificateUsosCode != null
        }.forEach {
            if (!entitlementDocumentRepository.existsByPersonAndType(person, it.certificateUsosCode!!)) {
                person.entitlementDocuments.add(
                        EntitlementDocument(
                                person = person,
                                issueDate = it.issueDate,
                                description = it.certificateType,
                                number = it.documentNumber,
                                type = it.certificateUsosCode,
                                school = it.issueInstitutionUsosCode?.let { schoolId ->
                                    schoolId.toLongOrNull()?.let { schoolIdLong ->
                                        schoolRepository.getOne(schoolIdLong)
                                    }
                                }
                        )
                )
            }
        }
    }

    private fun createOrUpdatePersonPhoto(person: Person, applicant: Applicant) {
        applicant.photo?.let {
            if (person.personPhoto != null) {
                person.personPhoto!!.photoBlob = irkService.getPhoto(it)
            } else {
                person.personPhoto = PersonPhoto(
                        person = person,
                        photoBlob = irkService.getPhoto(it)
                )
            }
            val personPreference = personPreferenceRepository.findByIdOrNull(PersonPreferenceId(
                    person.id!!, "photo_visibility"
            ))
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
                || applicant.applicantForeignerData.baseOfStay != "OKP") {
            return
        }
        val ownedDocument = ownedDocumentRepository.findByPersonAndAndDocumentType(
                person,
                documentTypeRepository.getOne(applicant.applicantForeignerData.baseOfStay!!)
        )
        if (ownedDocument != null) {
            ownedDocument.apply {
                documentType = documentTypeRepository.getOne(applicant.applicantForeignerData.baseOfStay!!)
                issueDate = applicant.applicantForeignerData.polishCardIssueDate
                issueCountry = applicant.applicantForeignerData.polishCardIssueCountry?.let { countryCode ->
                    citizenshipRepository.getOne(countryCode)
                }
                number = applicant.applicantForeignerData.polishCardNumber
                expirationDate = applicant.applicantForeignerData.polishCardValidTo
            }
        } else {
            applicant.applicantForeignerData.let {
                if (it.baseOfStay == null || it.baseOfStay != "OKP") {
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

    @Transactional(rollbackFor = [java.lang.Exception::class, java.lang.RuntimeException::class], propagation = Propagation.MANDATORY, transactionManager = "oracleTransactionManager")
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
            certificate: Document?,
            sourceOfFinancing: String?,
            basisOfAdmission: String?
    ): String {
        val student = studentService.createOrFindStudent(person, indexPoolCode)
        studentRepository.save(student)
        if (studentService.getPreviousStudyEndDate(person, dateOfAddmision) <= dateOfAddmision) {
            personProgrammeRepository.getDefaultProgramme(person.id!!)?.let {
                it.isDefault = 'N'
                personProgrammeRepository.save(it)
            }
        }
        val personProgramme = studentService.createPersonProgramme(
                person = person,
                programmeCode = programmeCode,
                startDate = startDate,
                dateOfAddmision = dateOfAddmision,
                didacticCycleCode = didacticCycleCode,
                stageCode = stageCode,
                student = student,
                certificate = certificate,
                sourceOfFinancing = sourceOfFinancing,
                basisOfAdmission = basisOfAdmission
        )
        irkApplication.personProgramme = personProgramme
        personProgramme.irkApplication = irkApplication
        personProgrammeRepository.save(personProgramme)
        return student.indexNumber
    }

    @Transactional(rollbackFor = [java.lang.Exception::class, RuntimeException::class], propagation = Propagation.REQUIRED, transactionManager = "oracleTransactionManager")
    fun processPerson(
            application: Application,
            dateOfAddmision: Date,
            didacticCycleCode: String,
            indexPoolCode: String,
            programmeCode: String,
            registration: String,
            stageCode: String,
            startDate: Date
    ): Pair<Person, String> {
        applicantService.check(application.applicant!!)
        var person: Person? = null
        application.applicant!!.usosId?.let { usosId ->
            person = personRepository.findByIdOrNull(usosId)
        }
        if (person == null) {
            application.applicant!!.basicData.pesel?.let { pesel ->
                person = personRepository.findOneByPesel(pesel)
            }
        }
        if (person == null) {
            application.applicant!!.additionalData.documentNumber?.let { idNumber ->
                person = personRepository.findOneByIdNumber(idNumber)
            }
        }
        if (person != null) {
            _self.update(application.applicant!!, person!!)
        } else {
            person = _self.create(application.applicant!!)
        }
        personRepository.save(person!!)
        val irkApplication = IrkApplication(
                applicationId = application.irkId,
                confirmationStatus = 0,
                irkInstance = application.irkInstance
        )
        val assignedIndexNumber = _self.immatriculate(
                person = person!!,
                dateOfAddmision = dateOfAddmision,
                didacticCycleCode = didacticCycleCode,
                indexPoolCode = indexPoolCode,
                programmeCode = programmeCode,
                registration = registration,
                stageCode = stageCode,
                startDate = startDate,
                irkApplication = irkApplication,
                certificate = application.certificate,
                sourceOfFinancing = application.applicationForeignerData?.sourceOfFinancing,
                basisOfAdmission = application.applicationForeignerData?.basisOfAdmission
        )
        if (setAsAccepted) {
            try {
                irkService.completeImmatriculation(application.irkId)
                irkApplication.confirmationStatus = 1
            } catch (e: HttpStatusCodeException) {
                val errorMessageDto = jacksonObjectMapper().readValue(e.responseBodyAsString, ErrorMessageDto::class.java)
                irkApplication.confirmationStatus = errorMessageDto.error
            }
        }
        irkApplicationRepository.save(irkApplication)
        return Pair(person!!, assignedIndexNumber)
    }
}