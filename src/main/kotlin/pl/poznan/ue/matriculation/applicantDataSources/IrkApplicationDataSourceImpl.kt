package pl.poznan.ue.matriculation.applicantDataSources

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.client.HttpClientErrorException
import pl.poznan.ue.matriculation.irk.dto.ErrorMessageDto
import pl.poznan.ue.matriculation.irk.dto.NotificationDto
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.irk.dto.registrations.RegistrationStatus
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicantMapper
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicationMapper
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto

open class IrkApplicationDataSourceImpl(
    private val irkService: IrkService,
    override val name: String,
    override val id: String,
    private val setAsAccepted: Boolean,
    private val irkApplicantMapper: IrkApplicantMapper,
    private val irkApplicationMapper: IrkApplicationMapper
) : IApplicationDataSource<IrkApplicationDTO, IrkApplicantDto>, IPhotoDownloader, INotificationSender {

    val logger: Logger = LoggerFactory.getLogger(IrkApplicationDataSourceImpl::class.java)

    override fun getApplicationsPage(
        import: Import,
        registrationCode: String,
        programmeForeignId: String,
        pageNumber: Int
    ): IPage<IrkApplicationDTO> {
        val page = irkService.getApplications(
            registration = registrationCode,
            programme = programmeForeignId,
            pageNumber = pageNumber
        )
        return object : IPage<IrkApplicationDTO> {
            override fun getTotalSize(): Int {
                return page.count
            }

            override fun getContent(): List<IrkApplicationDTO> {
                return page.results
            }

            override fun hasNext(): Boolean {
                return page.next != null
            }
        }
    }

    override fun getApplicantById(applicantId: Long, applicationDto: IrkApplicationDTO): IrkApplicantDto {
        return irkService.getApplicantById(applicantId)?.also {
            applicationDto.certificate?.let { documentDto -> it.educationData.documents.add(documentDto) }
        } ?: throw IllegalArgumentException("Unable to get applicant")
    }

    override fun getPhoto(photoUrl: String): ByteArray? {
        return try {
            irkService.getPhoto(photoUrl)
        } catch (e: HttpClientErrorException.NotFound) {
            null
        }
    }

    override fun postMatriculation(foreignApplicationId: Long): Int {
        if (!setAsAccepted) return 1
        return try {
            irkService.completeImmatriculation(foreignApplicationId)
            0
        } catch (e: HttpClientErrorException.BadRequest) {
            val errorMessageDto =
                jacksonObjectMapper().readValue(e.responseBodyAsString, ErrorMessageDto::class.java)
            return errorMessageDto.error
        }
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        return irkService.getAvailableRegistrationProgrammes(registration).map {
            ProgrammeDto(
                id = it,
                name = it,
                usosId = it
            )
        }
    }

    override fun getAvailableRegistrations(filter: String?): List<RegistrationDto> {
        val availableRegistrations = mutableListOf<RegistrationDto>()
        var currentPage = 1
        var hasNext: Boolean
        do {
            val page = irkService.getAvailableRegistrationsPage(
                pageNumber = currentPage,
                code = filter?.let { "^$filter".toRegex() },
                status = RegistrationStatus.ENDED
            )
            page?.results?.forEach {
                val registration = RegistrationDto(it.code, it.name.pl)
                availableRegistrations.add(registration)
            }
            hasNext = page?.next != null
            currentPage++
        } while (hasNext)
        return availableRegistrations
    }

    override fun getApplicationById(applicationId: Long, applicationDto: IrkApplicationDTO): IrkApplicationDTO? {
        return irkService.getApplication(applicationId)
    }

    override val instanceUrl = irkService.serviceUrl

    override fun mapApplicationDtoToApplication(applicationDto: IrkApplicationDTO): Application {
        return irkApplicationMapper.mapApplicationDtoToApplication(applicationDto)
    }

    override fun mapApplicantDtoToApplicant(applicantDto: IrkApplicantDto): Applicant {
        return irkApplicantMapper.mapApplicantDtoToApplicant(applicantDto)
    }

    override fun updateApplication(
        application: Application,
        applicationDto: IrkApplicationDTO
    ): Application {
        return irkApplicationMapper.update(application, applicationDto)
    }

    override fun updateApplicant(
        applicant: Applicant,
        applicantDto: IrkApplicantDto,
        applicationDto: IrkApplicationDTO
    ): Applicant {
        return irkApplicantMapper.update(applicant, applicantDto)
    }

    override fun getApplicationEditUrl(applicationId: Long): String {
        return "${instanceUrl}/pl/admin/application/${applicationId}/edit/"
    }

    override fun getPrimaryCertificate(
        application: Application,
        applicationDto: IrkApplicationDTO,
        applicant: Applicant,
        applicantDto: IrkApplicantDto,
        import: Import
    ): Document? {
        val pc = applicationDto.certificate ?: return null
        return applicant.documents.find {
            it.documentNumber == pc.documentNumber && it.certificateTypeCode == pc.certificateTypeCode
        }
    }

    override fun sendNotification(foreignApplicantId: Long, notificationDto: NotificationDto) {
        irkService.sendNotification(foreignApplicantId, notificationDto)
    }

    override fun getPrimaryIdentityDocument(
        application: Application,
        applicationDto: IrkApplicationDTO,
        applicant: Applicant,
        applicantDto: IrkApplicantDto,
        import: Import
    ): IdentityDocument? {
        return applicant.identityDocuments.find {
            it.number == applicantDto.additionalData.documentNumber
        }
    }
}
