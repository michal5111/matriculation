package pl.poznan.ue.matriculation.local.processor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.PersonChangeHistory
import pl.poznan.ue.matriculation.oracle.repo.*
import java.util.*

open class PersonProcessor(
    private val personRepository: PersonRepository,
    private val citizenshipRepository: CitizenshipRepository,
    private val schoolRepository: SchoolRepository,
    private val organizationalUnitRepository: OrganizationalUnitRepository,
    private val wkuRepository: WkuRepository
) : TargetSystemProcessor<Person> {

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitString: String

    @Value("\${pl.poznan.ue.matriculation.universityEmailSuffix}")
    lateinit var universityEmailSuffix: String

    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    override fun process(processRequest: ProcessRequest): ProcessResult<Person> {
        val person = createOrUpdatePerson(processRequest.application.applicant!!)
        return ProcessResult(person.id!!, null, person)
    }

    private val logger = LoggerFactory.getLogger(PersonProcessor::class.java)

    private fun createOrUpdatePerson(applicant: Applicant): Person {
        val person = personRepository.findOneByPeselOrIdNumberOrPersonId(
            applicant.usosId,
            applicant.pesel.orEmpty(),
            applicant.identityDocuments.filterNot {
                it.number.isNullOrBlank()
            }.map {
                it.number.orEmpty()
            }
        )
        return if (person != null) {
            logger.trace("Osoba istnieje. Aktualizuję")
            applicant.personExisted = true
            update(applicant, person)
        } else {
            logger.trace("Osoba nie istnieje. Tworzę")
            create(applicant)
        }
    }

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
            school = "${applicant.highSchoolType.orEmpty()} ${applicant.highSchoolName.orEmpty()} ${applicant.highSchoolCity.orEmpty()}"
                .trim()
                .takeIf {
                    applicant.highSchoolUsosCode == null && it.isNotBlank()
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
            militaryStatus = applicant.militaryStatus
        )
        return personRepository.save(person)
    }

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    open fun update(applicant: Applicant, person: Person): Person {
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
            changed = createOrUpdateIdentityDocument(this, applicant)
            logger.trace("Tworzę lub aktualizuję dokumenty uprawniające do podjęcia studiów")
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
            school = "${applicant.highSchoolType} ${applicant.highSchoolName} ${applicant.highSchoolCity}".takeIf {
                applicant.highSchoolUsosCode == null
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
        val identityDocument = applicant.primaryIdentityDocument
        if (identityDocument?.number != null) {
            if (person.documentType != identityDocument.type) {
                changed = true
                person.documentType = identityDocument.type
            }
            if (person.idNumber != identityDocument.number) {
                changed = true
                person.idNumber = identityDocument.number
            }
            person.identityDocumentExpirationDate = identityDocument.expDate
            person.identityDocumentIssuerCountry = identityDocument.country?.let { documentCountry ->
                citizenshipRepository.getReferenceById(documentCountry)
            }
        } else {
            person.identityDocumentExpirationDate?.let {
                if (it < Date()) {
                    person.apply {
                        identityDocumentIssuerCountry = null
                        identityDocumentExpirationDate = null
                        idNumber = null
                        documentType = null
                    }
                }
            }
            changed = true
        }
        return changed
    }
}
