package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ApplicantCheckException
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.mapper.ApplicantToPersonMapper
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.oracle.domain.Person
import java.util.*

@Service
class ApplicantService(
    private val applicantToPersonMapper: ApplicantToPersonMapper,
    private val applicantRepository: ApplicantRepository
) {

    fun findById(applicantId: Long): Applicant? {
        return applicantRepository.findByIdOrNull(applicantId)
    }

    val logger: Logger = LoggerFactory.getLogger(ApplicationService::class.java)

    fun createPersonFromApplicant(applicant: Applicant): Person {
        return applicantToPersonMapper.map(applicant)
    }

    fun check(applicant: Applicant) {
//        applicant.educationData.documents.forEach {
//            if (it.issueDate == null || it.documentNumber == null) {
//                //throw ApplicantCheckException("Brak daty lub numeru dokumentu uprawniającego do podjęcia studiów")
//                logger.warn("Brak daty lub numeru dokumentu uprawniającego do podjęcia studiów. Pomijam dodawnie Tego dokumentu. ApplicantId: ${it.educationData?.applicantId}")
//            }
//        }
        if (applicant.basicData.pesel.isNullOrBlank() && !applicant.identityDocuments.any { it.number != null }) {
            throw ApplicantCheckException("Brak peselu lub dokumentu tożsamości")
        }
    }

    fun clearPersonalData(applicant: Applicant): Applicant {
        applicant.apply {
            email = ""
            indexNumber = null
            password = null
            name.apply {
                middle = null
                family = ""
                maiden = null
                given = ""
            }
            citizenship = ""
            photo = null
            photoPermission = null
            modificationDate = Date()
            basicData.apply {
                cityOfBirth = null
                countryOfBirth = null
                dataSource = ""
                dateOfBirth = null
                pesel = null
                sex = 'M'
            }
            applicant.addresses.clear()
            phoneNumbers.clear()
            applicant.additionalData.apply {
                fathersName = null
                militaryCategory = null
                militaryStatus = null
                mothersName = null
                wku = null
            }
            applicant.applicantForeignerData?.apply {
                baseOfStay = null
                foreignerStatus.clear()
                polishCardIssueCountry = null
                polishCardIssueDate = null
                polishCardNumber = null
                polishCardValidTo = null
            }
            applicant.educationData.apply {
                documents.clear()
                highSchoolCity = null
                highSchoolName = null
                highSchoolType = null
                highSchoolUsosCode = null
            }
            applicant.erasmusData?.apply {
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
            applicant.identityDocuments.clear()
        }
        return applicant
    }
}
