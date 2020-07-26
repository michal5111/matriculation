package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ApplicantCheckException
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.mapper.ApplicantToPersonMapper
import pl.poznan.ue.matriculation.oracle.domain.Person
import java.util.*

@Service
class ApplicantService(
        private val applicantToPersonMapper: ApplicantToPersonMapper
) {

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
        if (applicant.basicData.pesel.isNullOrBlank() && applicant.additionalData.documentNumber.isNullOrBlank()) {
            throw ApplicantCheckException("Brak peselu lub dokumentu tożsamości")
        }
    }

    fun clearPersonalData(applicant: Applicant): Applicant {
        applicant.apply {
            email = ""
            indexNumber = ""
            password = ""
            name.apply {
                middle = ""
                family = ""
                maiden = ""
                given = ""
            }
            phone = ""
            citizenship = ""
            photo = ""
            photoPermission = ""
            casPasswordOverride = ""
            modificationDate = Date()
            basicData.apply {
                cityOfBirth = ""
                countryOfBirth = ""
                dataSource = ""
                dateOfBirth = Date()
                pesel = ""
                sex = 'M'
            }
            applicant.addresses.clear()
            phoneNumbers.clear()
            applicant.additionalData.apply {
                countryOfBirth = ""
                cityOfBirth = ""
                documentCountry = ""
                documentExpDate = Date()
                documentNumber = ""
                documentType = 'P'
                fathersName = ""
                militaryCategory = ""
                militaryStatus = ""
                mothersName = ""
                wku = ""
            }
            applicant.applicantForeignerData?.apply {
                baseOfStay = ""
                foreignerStatus.clear()
                polishCardIssueCountry = ""
                polishCardIssueDate = Date()
                polishCardNumber = ""
                polishCardValidTo = Date()
            }
            applicant.educationData.apply {
                documents.clear()
                highSchoolCity = ""
                highSchoolName = ""
                highSchoolType = ""
                highSchoolUsosCode = -1L
            }
        }
        return applicant
    }
}