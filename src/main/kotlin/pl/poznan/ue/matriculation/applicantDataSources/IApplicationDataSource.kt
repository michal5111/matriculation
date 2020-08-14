package pl.poznan.ue.matriculation.applicantDataSources

import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.AbstractApplicantDto
import pl.poznan.ue.matriculation.local.dto.AbstractApplicationDto
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto

interface IApplicationDataSource<applicationDTO : AbstractApplicationDto, applicantDTO : AbstractApplicantDto> {
    fun getApplicationsPage(registrationCode: String, programmeForeignId: String, pageNumber: Int): IPage<applicationDTO>

    fun getApplicantById(applicantId: Long): applicantDTO

    fun getPhoto(photoUrl: String): ByteArray?

    fun getName(): String

    fun getId(): String

    fun postMatriculation(applicationId: Long): Int

    fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto>

    fun getAvailableRegistrations(): List<RegistrationDto>

    fun getApplicationById(applicationId: Long): applicationDTO?

    fun mapApplicationDtoToApplication(applicationDto: applicationDTO): Application

    fun mapApplicantDtoToApplicant(applicantDto: applicantDTO): Applicant

    fun updateApplication(application: Application, applicationDto: applicationDTO): Application

    fun updateApplicant(applicant: Applicant, applicantDto: applicantDTO): Applicant

    fun getInstanceUrl(): String

    fun preprocess(applicationDto: applicationDTO, applicantDto: applicantDTO)

    fun getPrimaryCertificate(application: Application, applicationDto: applicationDTO, applicant: Applicant, applicantDto: applicantDTO, import: Import): Document?

    fun getApplicationEditUrl(applicationId: Long): String
}