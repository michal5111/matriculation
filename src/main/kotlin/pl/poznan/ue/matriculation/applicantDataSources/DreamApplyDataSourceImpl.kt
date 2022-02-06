package pl.poznan.ue.matriculation.applicantDataSources

import org.springframework.web.client.HttpClientErrorException
import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.EducationLevelType
import pl.poznan.ue.matriculation.dreamApply.dto.email.EmailDto
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicantMapper
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicationMapper
import pl.poznan.ue.matriculation.dreamApply.service.DreamApplyService
import pl.poznan.ue.matriculation.irk.dto.NotificationDto
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto

open class DreamApplyDataSourceImpl(
    override val name: String,
    override val id: String,
    private val dreamApplyService: DreamApplyService,
    private val applicantMapper: DreamApplyApplicantMapper,
    private val applicationMapper: DreamApplyApplicationMapper,
    private val status: String
) : IApplicationDataSource<DreamApplyApplicationDto, DreamApplyApplicantDto>, IPhotoDownloader, INotificationSender {

    override val instanceUrl = dreamApplyService.instanceUrl

    override fun getApplicationsPage(
        import: Import,
        registrationCode: String,
        programmeForeignId: String,
        pageNumber: Int
    ): IPage<DreamApplyApplicationDto> {
        val applicationMap = dreamApplyService.getApplicationsByFilter(
            academicTermID = registrationCode,
            additionalFilters = mapOf(
                "byCourseIDs" to programmeForeignId,
                "byOfferType" to status
            )
        ) ?: throw java.lang.IllegalArgumentException("Unable to get applicants")
        val applications = applicationMap.values.filter { dreamApplyApplicationDto ->
            val applicationOffers = dreamApplyService.getApplicationOffers(dreamApplyApplicationDto.offers)
            applicationOffers!!.any {
                it.value.course == "/api/courses/$programmeForeignId"
                    && it.value.type == status
            }
        }
        return object : IPage<DreamApplyApplicationDto> {
            override fun getSize(): Int {
                return applications.size
            }

            override fun getResultsList(): List<DreamApplyApplicationDto> {
                return applications
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

    override fun postMatriculation(foreignApplicationId: Long): Int {
        return 1
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        val courses = dreamApplyService.getCourses(statuses = "Online")
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

    override fun preprocess(applicationDto: DreamApplyApplicationDto, applicantDto: DreamApplyApplicantDto) {
        applicantDto.dreamApplyApplication = getApplicationById(applicationDto.id)
    }

    override fun getApplicationEditUrl(applicationId: Long): String {
        return "${instanceUrl}/application/view/id/$applicationId"
    }

    override fun getPrimaryCertificate(
        application: Application,
        applicationDto: DreamApplyApplicationDto,
        applicant: Applicant,
        applicantDto: DreamApplyApplicantDto,
        import: Import
    ): Document? {
        val programmeLevel = import.programmeCode.substring(1, 2)
        val levelType = EducationLevelType.values().find {
            it.programmeLevel == programmeLevel
        }
        return applicant.documents.find {
            it.certificateUsosCode == levelType?.usosCode
        }
    }

    override fun sendNotification(foreignApplicantId: Long, notificationDto: NotificationDto) {
        val emailDto = EmailDto(
            subject = notificationDto.header,
            message = notificationDto.message
        )
        dreamApplyService.sendEmail(foreignApplicantId, emailDto)
    }

    override fun getPrimaryIdentityDocument(
        application: Application,
        applicationDto: DreamApplyApplicationDto,
        applicant: Applicant,
        applicantDto: DreamApplyApplicantDto,
        import: Import
    ): IdentityDocument? {
        return applicant.identityDocuments.firstOrNull()
    }
}
