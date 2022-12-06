package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ApplicantCheckException
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import java.util.*

@Service
class ApplicantService(
    private val applicantRepository: ApplicantRepository
) {

    val logger: Logger = LoggerFactory.getLogger(ApplicationService::class.java)

    fun findById(applicantId: Long): Applicant? {
        return applicantRepository.findByIdOrNull(applicantId)
    }

    fun findWithIdentityDocumentsById(applicantId: Long): Applicant? {
        return applicantRepository.findWithIdentityDocumentsById(applicantId)
    }

    fun check(applicant: Applicant) {
        if (applicant.pesel.isNullOrBlank() && !applicant.identityDocuments.any { it.number != null }) {
            throw ApplicantCheckException("Brak peselu lub dokumentu tożsamości")
        }
        if (applicant.email.isBlank()) {
            throw ApplicantCheckException("Brak adresu email")
        }
    }

    fun anonymize(applicant: Applicant) = applicant.apply {
        email = "$id@anonymous.pl"
        indexNumber = null
        password = null

        middle = null
        family = "Anonymous"
        maiden = null
        given = "Anonymous"

        citizenship = null
        photo = null
        photoPermission = null
        modificationDate = Date()

        cityOfBirth = null
        countryOfBirth = null
        dateOfBirth = null
        pesel = null
        sex = 'M'

        addresses.clear()
        phoneNumbers.clear()
        primaryIdentityDocument = null
        fathersName = null
        militaryCategory = null
        militaryStatus = null
        mothersName = null
        wku = null
        applicantForeignerData?.apply {
            baseOfStay = null
            foreignerStatus.clear()
            polishCardIssueCountry = null
            polishCardIssueDate = null
            polishCardNumber = null
            polishCardValidTo = null
        }
        documents.clear()
        highSchoolCity = null
        highSchoolName = null
        highSchoolType = null
        highSchoolUsosCode = null
        erasmusData?.apply {
            accommodationPreference = null
            homeInstitution?.apply {
                departmentName = null
                erasmusCode = null
                country = null
                address = null
            }
            coordinatorData?.apply {
                email = null
                name = null
                phone = null
            }
            courseData?.apply {
                level = null
                name = null
                term = null
            }
        }
        identityDocuments.clear()
    }

    fun save(applicant: Applicant): Applicant {
        return applicantRepository.save(applicant)
    }

    fun findByForeignIdAndDataSourceId(foreignId: Long, dataSourceId: String): Applicant? {
        return applicantRepository.findByForeignIdAndDataSourceId(foreignId, dataSourceId)
    }

    @Transactional
    fun deleteOrphaned() {
        val applicants = applicantRepository.findAllOrphaned()
        applicantRepository.deleteAll(applicants)
    }

    @Transactional
    fun deleteOrphanedById(id: Long) {
        val applicant = applicantRepository.findOrphanedById(id)
        applicant?.let {
            applicantRepository.delete(applicant)
        }
    }
}
