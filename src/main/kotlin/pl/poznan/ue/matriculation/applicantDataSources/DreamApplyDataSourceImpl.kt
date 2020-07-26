package pl.poznan.ue.matriculation.applicantDataSources

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.ApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.ApplicationDto
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicantMapper
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicationMapper
import pl.poznan.ue.matriculation.dreamApply.service.DreamApplyService
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto
import pl.poznan.ue.matriculation.oracle.domain.IrkApplication

class DreamApplyDataSourceImpl(
        private val name: String,
        private val id: String,
        private val dreamApplyService: DreamApplyService
) : IApplicationDataSource<ApplicationDto, ApplicantDto> {

    private val applicantMapper = DreamApplyApplicantMapper()
    private val applicationMapper = DreamApplyApplicationMapper()

    override fun getApplicationsPage(registrationId: String, programmeId: String, pageNumber: Int): IPage<ApplicationDto> {
        val academicTermDto = dreamApplyService.getAcademicTermById(registrationId.toLong())
                ?: throw java.lang.IllegalArgumentException("Unable to get academic terms")
        val applicationMap = dreamApplyService.getApplicationsByFilter(
                academicTermID = registrationId,
                academicYear = academicTermDto.year
        ) ?: throw java.lang.IllegalArgumentException("Unable to get applicants")
        val applicationList = applicationMap.map {
            it.value.id = it.key
            it.value
        }
        return object : IPage<ApplicationDto> {
            override fun getSize(): Int {
                return applicationList.size
            }

            override fun getResultsList(): List<ApplicationDto> {
                return applicationList
            }

            override fun hasNext(): Boolean {
                return false
            }
        }
    }

    override fun getApplicantById(applicantId: Long): ApplicantDto {
        return dreamApplyService.getApplicantById(applicantId) ?: throw IllegalArgumentException("Applicant not found")
    }

    override fun getPhoto(photoUrl: String): ByteArray {
        return dreamApplyService.getPhoto(photoUrl)
    }

    override fun getName(): String {
        return name
    }

    override fun getId(): String {
        return id
    }

    override fun postMatriculation(applicationId: Long, irkApplication: IrkApplication) {
        TODO("Not yet implemented")
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        TODO("Not yet implemented")
    }

    override fun getAvailableRegistrations(): List<RegistrationDto> {
        val academicTermsMap = dreamApplyService.getAcademicTerms()
                ?: throw IllegalStateException("Unable to download academic terms")
        return academicTermsMap.map {
            RegistrationDto(
                    id = it.key.toString(),
                    name = "${it.value.name} ${it.value.year}"
            )
        }
    }

    override fun getApplicationById(applicationId: Long): ApplicationDto? {
        return dreamApplyService.getApplicationById(applicationId)
    }

    override fun mapApplicationDtoToApplication(applicationDto: ApplicationDto): Application {
        return applicationMapper.map(applicationDto)
    }

    override fun mapApplicantDtoToApplicant(applicantDto: ApplicantDto): Applicant {
        return applicantMapper.map(applicantDto)
    }

    override fun updateApplication(application: Application, applicationDto: ApplicationDto): Application {
        return applicationMapper.update(application, applicationDto)
    }

    override fun updateApplicant(applicant: Applicant, applicantDto: ApplicantDto): Applicant {
        return applicantMapper.update(applicant, applicantDto)
    }

    override fun getInstanceUrl(): String {
        return dreamApplyService.instanceUrl
    }

    override fun preprocess(applicationDto: ApplicationDto, applicantDto: ApplicantDto) {
        applicantDto.application = applicationDto
    }

    override fun getPrimaryCertificate(applicationId: Long, documents: List<Document>): Document? {
        TODO("Not yet implemented")
    }
}