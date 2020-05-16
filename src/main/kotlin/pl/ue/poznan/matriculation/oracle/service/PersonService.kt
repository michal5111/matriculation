package pl.ue.poznan.matriculation.oracle.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.ue.poznan.matriculation.irk.mapper.ApplicantMapper
import pl.ue.poznan.matriculation.irk.mapper.StudentMapper
import pl.ue.poznan.matriculation.irk.service.IrkService
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant
import pl.ue.poznan.matriculation.local.domain.applications.Application
import pl.ue.poznan.matriculation.local.domain.import.Import
import pl.ue.poznan.matriculation.local.service.ApplicantService
import pl.ue.poznan.matriculation.oracle.domain.*
import pl.ue.poznan.matriculation.oracle.repo.*
import java.util.*
import javax.annotation.PostConstruct

@Service
class PersonService(
        private val personRepository: PersonRepository,
        private val applicantMapper: ApplicantMapper,
        private val studentMapper: StudentMapper,
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
        private val addressService: AddressService
) {

    @Autowired
    private lateinit var _self: PersonService
    private lateinit var permanentAddressType: AddressType
    private lateinit var correspondenceAddressType: AddressType
    private lateinit var defaultStudentOrganizationalUnit: OrganizationalUnit

    @Value("\${pl.ue.poznan.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitString: String

    fun mapPerson(applicant: Applicant): Person {
        return applicantMapper.applicantToPersonMapper(applicant)
    }

    @PostConstruct
    fun init() {
        permanentAddressType = addressTypeRepository.getOne("STA")
        correspondenceAddressType = addressTypeRepository.getOne("KOR")
        defaultStudentOrganizationalUnit = organizationalUnitRepository.getOne(defaultStudentOrganizationalUnitString)
    }

    @Transactional(rollbackFor = [java.lang.Exception::class], propagation = Propagation.REQUIRED, transactionManager = "oracleTransactionManager")
    fun updatePerson(applicant: Applicant, person: Person) {
        applicantService.checkApplicant(applicant)
        person.apply {
            email = applicant.email
            name = applicant.name.given!!
            middleName = applicant.name.middle
            surname = applicant.name.family!!
            citizenship = citizenshipRepository.getOne(applicant.citizenship!!)
            birthDate = applicant.basicData.dateOfBirth
            birthCity = applicant.basicData.cityOfBirth
            birthCountry = citizenshipRepository.getOne(applicant.basicData.countryOfBirth)
            sex = applicant.basicData.sex
            nationality = citizenshipRepository.getOne(applicant.basicData.countryOfBirth)
            organizationalUnit = defaultStudentOrganizationalUnit
            middleSchool = applicant.educationData.highSchoolUsosCode?.let {
                schoolRepository.getOne(it)
            }
            updateAddresses(this, applicant)
            updatePhoneNumbers(this, applicant)

            //dok toż

            mothersName = applicant.additionalData.mothersName
            fathersName = applicant.additionalData.fathersName
            wku = applicant.additionalData.wku?.let {
                wkuRepository.getOne(it)
            }
            updateEntitlementDocument(person, applicant)
            militaryCategory = applicant.additionalData.militaryCategory
            militaryStatus = applicant.additionalData.militaryStatus
            updatePersonPhoto(person, applicant)
        }
    }

    private fun updateAddresses(person: Person, applicant: Applicant) {
        val permanentAddress = addressRepository.findByPersonAndAddressType(person, permanentAddressType)
        if (permanentAddress != null) {
            addressService.updateAddress(
                    address = permanentAddress,
                    city = applicant.contactData.officialCity,
                    street = applicant.contactData.officialStreet,
                    houseNumber = applicant.contactData.officialStreetNumber,
                    apartmentNumber = applicant.contactData.officialFlatNumber,
                    zipCode = applicant.contactData.officialPostCode,
                    cityIsCity = applicant.contactData.officialCityIsCity,
                    countryCode = applicant.contactData.officialCountry
            )
        } else {
            person.addresses.add(
                    addressService.createAddress(
                            addressType = permanentAddressType,
                            person = person,
                            city = applicant.contactData.officialCity,
                            street = applicant.contactData.officialStreet,
                            houseNumber = applicant.contactData.officialStreetNumber,
                            apartmentNumber = applicant.contactData.officialFlatNumber,
                            zipCode = applicant.contactData.officialPostCode,
                            cityIsCity = applicant.contactData.officialCityIsCity,
                            countryCode = applicant.contactData.officialCountry
                    )
            )
        }
        val correspondenceAddress = addressRepository.findByPersonAndAddressType(person, correspondenceAddressType)
        if (correspondenceAddress != null) {
            addressService.updateAddress(
                    address = correspondenceAddress,
                    city = applicant.contactData.realCity,
                    street = applicant.contactData.realStreet,
                    houseNumber = applicant.contactData.realStreetNumber,
                    apartmentNumber = applicant.contactData.realFlatNumber,
                    zipCode = applicant.contactData.realPostCode,
                    cityIsCity = applicant.contactData.realCityIsCity,
                    countryCode = applicant.contactData.realCountry
            )
        } else {
            person.addresses.add(
                    addressService.createAddress(
                            person = person,
                            addressType = correspondenceAddressType,
                            city = applicant.contactData.realCity,
                            street = applicant.contactData.realStreet,
                            houseNumber = applicant.contactData.realStreetNumber,
                            apartmentNumber = applicant.contactData.realFlatNumber,
                            zipCode = applicant.contactData.realPostCode,
                            cityIsCity = applicant.contactData.realCityIsCity,
                            countryCode = applicant.contactData.realCountry
                    )
            )
        }
    }

    private fun updatePhoneNumbers(person: Person, applicant: Applicant) {
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

    private fun updateEntitlementDocument(person: Person, applicant: Applicant) {
        applicant.educationData.documents.filter {
            it.certificateUsosCode != null
        }.forEach {
            if (!entitlementDocumentRepository.existsByPersonAndType(person, it.certificateUsosCode!!)) {
                person.entitlementDocuments.add(
                        EntitlementDocument(
                                person = person,
                                issueDate = it.issueDate!!,
                                description = it.certificateType,
                                number = it.documentNumber!!,
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

    private fun updatePersonPhoto(person: Person, applicant: Applicant) {
        applicant.photo?.let {
            if (person.personPhoto != null) {
                person.personPhoto!!.photoBlob = irkService.getPhoto(it)
            } else {
                person.personPhoto = PersonPhoto(
                        person = person,
                        photoBlob = irkService.getPhoto(it)
                )
            }
        }
    }

    @Transactional(rollbackFor = [java.lang.Exception::class], propagation = Propagation.REQUIRED, transactionManager = "oracleTransactionManager")
    fun immatriculate(
            person: Person,
            programmeCode: String,
            registration: String,
            indexPoolCode: String,
            startDate: Date,
            dateOfAddmision: Date,
            stageCode: String,
            didacticCycleCode: String,
            irkApplication: IrkApplication
    ): String {
        val student = studentMapper.createOrFindStudent(person, indexPoolCode)
        studentRepository.save(student)
        val personProgramme = studentMapper.createPersonProgramme(
                person = person,
                programmeCode = programmeCode,
                startDate = startDate,
                dateOfAddmision = dateOfAddmision,
                didacticCycleCode = didacticCycleCode,
                stageCode = stageCode,
                student = student,
                irkApplication = irkApplication
        )
        personProgrammeRepository.getDefaultProgramme(person.id!!)?.let {
            it.isDefault = 'N'
            personProgrammeRepository.save(it)
        }
        personProgrammeRepository.save(personProgramme)
        return student.indexNumber
    }

    @Transactional(rollbackFor = [java.lang.Exception::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "oracleTransactionManager")
    fun processPerson(import: Import, application: Application): Pair<Long?, String> {
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
            _self.updatePerson(application.applicant!!, person!!)
        } else {
            person = _self.mapPerson(application.applicant!!)
        }
        personRepository.save(person!!)
        val irkApplication = IrkApplication(
                applicationId = application.irkId,
                confirmationStatus = 1,
                irkInstance = application.irkInstance
        )
        val assignedIndexNumber = _self.immatriculate(
                person = person!!,
                dateOfAddmision = import.dateOfAddmision,
                didacticCycleCode = import.didacticCycleCode,
                indexPoolCode = import.indexPoolCode,
                programmeCode = import.programmeCode,
                registration = import.registration,
                stageCode = import.stageCode,
                startDate = import.startDate,
                irkApplication = irkApplication
        )
        return Pair(person!!.id, assignedIndexNumber)
    }
}