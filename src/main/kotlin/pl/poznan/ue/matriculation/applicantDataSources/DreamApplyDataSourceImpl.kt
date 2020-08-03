package pl.poznan.ue.matriculation.applicantDataSources

import org.springframework.web.client.HttpClientErrorException
import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
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
) : IApplicationDataSource<DreamApplyApplicationDto, DreamApplyApplicantDto> {

    private val applicantMapper = DreamApplyApplicantMapper()
    private val applicationMapper = DreamApplyApplicationMapper()

    override fun getApplicationsPage(registrationCode: String, programmeForeignId: String, pageNumber: Int): IPage<DreamApplyApplicationDto> {
        val applicationMap = dreamApplyService.getApplicationsByFilter(
                academicTermID = registrationCode,
                additionalFilters = mapOf(
                        "byCourseIDs" to programmeForeignId,
                        "byOfferTypes" to "Enrolled",
                        "byOfferDecisions" to "Final"
                )
        ) ?: throw java.lang.IllegalArgumentException("Unable to get applicants")
        val applications = applicationMap.values.filter { dreamApplyApplicationDto ->
            val applicationOffers = dreamApplyService.getApplicationOffers(dreamApplyApplicationDto.offers)
            applicationOffers!!.any {
                it.value.course == "/api/courses/$programmeForeignId"
                        && it.value.type == "Enrolled"
                        && it.value.decision == "Final"
            }
        }
        return object : IPage<DreamApplyApplicationDto> {
            override fun getSize(): Int {
                return applications.size
            }

            override fun getResultsList(): List<DreamApplyApplicationDto> {
                return applications.toList()
            }

            override fun hasNext(): Boolean {
                return false
            }
        }
    }

    override fun getApplicantById(applicantId: Long): DreamApplyApplicantDto {
        return dreamApplyService.getApplicantById(applicantId) ?: throw IllegalArgumentException("Applicant not found")
    }

    override fun getPhoto(photoUrl: String): ByteArray? {
        return try {
            dreamApplyService.getPhoto(photoUrl)
        } catch (e: HttpClientErrorException.NotFound) {
            null
        }
    }

    override fun getName(): String {
        return name
    }

    override fun getId(): String {
        return id
    }

    override fun postMatriculation(applicationId: Long, irkApplication: IrkApplication) {
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        val courses = dreamApplyService.getCourses()
        return courses!!.values.filter {
            it.code != null
        }.map {
            ProgrammeDto(
                    id = it.id.toString(),
                    name = "${it.code} ${it.name}",
                    usosId = it.code!!
            )
        }
    }

    override fun getAvailableRegistrations(): List<RegistrationDto> {
        val academicTermsMap = dreamApplyService.getAcademicTerms()
                ?: throw IllegalStateException("Unable to download academic terms")
        return academicTermsMap.map {
            RegistrationDto(
                    id = it.key.toString(),
                    name = it.value.name
            )
        }.sortedByDescending {
            it.id.toLong()
        }
    }

    override fun getApplicationById(applicationId: Long): DreamApplyApplicationDto? {
        return dreamApplyService.getApplicationById(applicationId)
    }

    override fun mapApplicationDtoToApplication(applicationDto: DreamApplyApplicationDto): Application {
        return applicationMapper.map(applicationDto)
    }

    override fun mapApplicantDtoToApplicant(applicantDto: DreamApplyApplicantDto): Applicant {
        return applicantMapper.map(applicantDto)
    }

    override fun updateApplication(application: Application, applicationDto: DreamApplyApplicationDto): Application {
        return applicationMapper.update(application, applicationDto)
    }

    override fun updateApplicant(applicant: Applicant, applicantDto: DreamApplyApplicantDto): Applicant {
        return applicantMapper.update(applicant, applicantDto)
    }

    override fun getInstanceUrl(): String {
        return dreamApplyService.instanceUrl
    }

    override fun preprocess(applicationDto: DreamApplyApplicationDto, applicantDto: DreamApplyApplicantDto) {
        println("https://apply.ue.poznan.pl/api/applicants/${applicantDto.id}")
        println("https://apply.ue.poznan.pl/api/applications/${applicationDto.id}")
        applicantDto.dreamApplyApplication = getApplicationById(applicationDto.id)
    }

    override fun getPrimaryCertificate(applicationId: Long, documents: List<Document>): Document? {
        return null
    }

    override fun getApplicationEditUrl(applicationId: Long): String {
        return "${getInstanceUrl()}/application/view/id/$applicationId"
    }
}