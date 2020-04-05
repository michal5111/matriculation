package pl.ue.poznan.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.ue.poznan.matriculation.exception.ApplicantCheckException
import pl.ue.poznan.matriculation.irk.dto.applicants.ApplicantDTO
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant
import pl.ue.poznan.matriculation.local.domain.applicants.Document
import pl.ue.poznan.matriculation.local.repo.DocumentRepository

@Service
class ApplicantService(
        private val documentRepository: DocumentRepository
) {

    val logger: Logger = LoggerFactory.getLogger(ApplicationService::class.java)

    fun checkApplicant(applicant: Applicant) {
        applicant.educationData.documents.forEach {
            if (it.issueDate == null || it.documentNumber == null) {
                throw ApplicantCheckException("Brak daty lub numeru dokumentu uprawniającego do podjęcia studiów")
            }
        }
        if (applicant.basicData.pesel == null && applicant.additionalData.documentNumber == null) {
            throw ApplicantCheckException("Brak peselu lub dokumentu tożsamości")
        }
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun updateApplicant(applicant: Applicant, applicantDTO: ApplicantDTO): Applicant {
        applicant.apply {
            email = applicantDTO.email
            indexNumber = applicantDTO.indexNumber
            password = applicantDTO.password
            name.apply {
                middle = applicantDTO.name.middle
                family = applicantDTO.name.family
                maiden = applicantDTO.name.maiden
                given = applicantDTO.name.given
            }
            phone = applicantDTO.phone
            citizenship = applicantDTO.citizenship
            photo = applicantDTO.photo
            photoPermission = applicantDTO.photoPermission
            casPasswordOverride = applicantDTO.casPasswordOverride
            modificationDate = applicantDTO.modificationDate
            basicData.apply {
                cityOfBirth = applicantDTO.basicData.cityOfBirth
                countryOfBirth = applicantDTO.basicData.countryOfBirth
                dataSource = applicantDTO.basicData.dataSource
                dateOfBirth = applicantDTO.basicData.dateOfBirth
                pesel = applicantDTO.basicData.pesel
                sex = applicantDTO.basicData.sex
            }
            applicantDTO.contactData.let {
                applicant.contactData.apply {
                    modificationDate = it.modificationDate
                    officialCity = it.officialCity
                    officialCityIsCity = it.officialCityIsCity
                    officialCountry = it.officialCountry
                    officialFlatNumber = it.officialFlatNumber
                    officialPostCode = it.officialPostCode
                    officialStreet = it.officialStreet
                    officialStreetNumber = it.officialStreetNumber
                    phoneNumber = it.phoneNumber
                    phoneNumber2 = it.phoneNumber2
                    phoneNumberType = it.phoneNumberType
                    phoneNumber2Type = it.phoneNumber2Type
                    realCity = it.realCity
                    realCityIsCity = it.realCityIsCity
                    realCountry = it.realCountry
                    realFlatNumber = it.realFlatNumber
                    realPostCode = it.realPostCode
                    realStreet = it.realStreet
                    realStreetNumber = it.realStreetNumber
                }
            }
            applicantDTO.additionalData.let {
                applicant.additionalData.apply {
                    countryOfBirth = it.countryOfBirth
                    cityOfBirth = it.cityOfBirth
                    documentCountry = it.documentCountry
                    documentExpDate = it.documentExpDate
                    documentNumber = it.documentNumber
                    documentType = it.documentType
                    fathersName = it.fathersName
                    militaryCategory = it.militaryCategory
                    militaryStatus = it.militaryStatus
                    mothersName = it.mothersName
                    wku = it.wku
                }
            }
            applicantDTO.foreignerData?.let {
                applicant.applicantForeignerData?.apply {
                    baseOfStay = it.baseOfStay
//                    it.foreignerStatus.map { statusDto ->
//                        Status(
//                                status = statusDto.status
//                        )
//                    }
                    polishCardIssueCountry = it.polishCardIssueCountry
                    polishCardIssueDate = it.polishCardIssueDate
                    polishCardNumber = it.polishCardNumber
                    polishCardValidTo = it.polishCardValidTo
                }
            }
            applicantDTO.educationData.let {
                applicant.educationData.apply {
                    documentRepository.deleteAllByEducationData(this)
                    it.documents = listOf()
                    it.documents.forEach { documentDto ->
                        educationData.documents.add(
                                Document(
                                        educationData = this,
                                        certificateType = documentDto.certificateType,
                                        certificateTypeCode = documentDto.certificateTypeCode,
                                        certificateUsosCode = documentDto.certificateUsosCode,
                                        comment = documentDto.comment,
                                        documentNumber = documentDto.documentNumber,
                                        documentYear = documentDto.documentYear,
                                        issueCity = documentDto.issueCity,
                                        issueCountry = documentDto.issueCountry,
                                        issueDate = documentDto.issueDate,
                                        issueInstitution = documentDto.issueInstitution,
                                        issueInstitutionUsosCode = documentDto.issueInstitutionUsosCode,
                                        modificationDate = documentDto.modificationDate
                                )
                        )

                    }
                    highSchoolCity = it.highSchoolCity
                    highSchoolName = it.highSchoolName
                    highSchoolType = it.highSchoolType
                    highSchoolUsosCode = it.highSchoolUsosCode
                }
            }
        }
        return applicant
    }
}