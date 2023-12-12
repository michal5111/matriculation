package pl.poznan.ue.matriculation.applicantDataSources

import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.*

interface IApplicationDataSource<applicationDTO : IApplicationDto, applicantDTO : IApplicantDto> {
    val name: String

    val id: String

    val instanceUrl: String

    val additionalParameters: List<DataSourceAdditionalParameter>
        get() = listOf()

    fun getApplicationsPage(
        import: Import,
        registrationCode: String,
        programmeForeignId: String,
        pageNumber: Int
    ): IPage<applicationDTO>

    fun getApplicantById(applicantId: Long, applicationDto: applicationDTO): applicantDTO

    fun postMatriculation(foreignApplicationId: Long): Int

    fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto>

    fun getAvailableRegistrations(filter: String?): List<RegistrationDto>

    fun getApplicationById(applicationId: Long, applicationDto: applicationDTO): applicationDTO?

    fun mapApplicationDtoToApplication(applicationDto: applicationDTO): Application

    fun mapApplicantDtoToApplicant(applicantDto: applicantDTO): Applicant

    fun updateApplication(application: Application, applicationDto: applicationDTO): Application

    fun updateApplicant(applicant: Applicant, applicantDto: applicantDTO, applicationDto: applicationDTO): Applicant

    fun getPrimaryCertificate(
        application: Application,
        applicationDto: applicationDTO,
        applicant: Applicant,
        applicantDto: applicantDTO,
        import: Import
    ): Document?

    fun getPrimaryIdentityDocument(
        application: Application,
        applicationDto: applicationDTO,
        applicant: Applicant,
        applicantDto: applicantDTO,
        import: Import
    ): IdentityDocument?

    fun getApplicationEditUrl(applicationId: Long): String?
}
