package pl.poznan.ue.matriculation.applicantDataSources

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.client.HttpStatusCodeException
import pl.poznan.ue.matriculation.irk.dto.ErrorMessageDto
import pl.poznan.ue.matriculation.irk.dto.NotificationDto
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicantMapper
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicationMapper
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
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

    override fun getApplicationsPage(
        import: Import,
        registrationCode: String,
        programmeForeignId: String,
        pageNumber: Int
    ): IPage<IrkApplicationDTO> {
        val page = irkService.getApplications(
            admitted = true,
            paid = true,
            qualified = true,
            registration = registrationCode,
            programme = programmeForeignId,
            pageNumber = pageNumber,
            pageLength = 20
        )
        return object : IPage<IrkApplicationDTO> {
            override fun getSize(): Int {
                return page.count
            }

            override fun getResultsList(): List<IrkApplicationDTO> {
                return page.results
            }

            override fun hasNext(): Boolean {
                return page.next != null
            }
        }
    }

    override fun getApplicantById(applicantId: Long): IrkApplicantDto {
        return irkService.getApplicantById(applicantId) ?: throw IllegalArgumentException("Unable to get applicant")
    }

    override fun getPhoto(photoUrl: String): ByteArray? {
        return irkService.getPhoto(photoUrl)
    }

    override fun postMatriculation(foreignApplicationId: Long): Int {
        if (!setAsAccepted) return 1
        return try {
            irkService.completeImmatriculation(foreignApplicationId)
            1
        } catch (e: HttpStatusCodeException) {
            val errorMessageDto = jacksonObjectMapper().readValue(e.responseBodyAsString, ErrorMessageDto::class.java)
            errorMessageDto.error
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

    override fun getAvailableRegistrations(): List<RegistrationDto> {
        val availableRegistrations = mutableListOf<RegistrationDto>()
        var currentPage = 1
        var hasNext: Boolean
        do {
            val page = irkService.getAvailableRegistrationsPage(currentPage)
            page?.results?.forEach {
                val registration = RegistrationDto(it.code, it.name.pl!!)
                availableRegistrations.add(registration)
            }
            hasNext = page?.next != null
            currentPage++
        } while (hasNext)
        return availableRegistrations
    }

    override fun getApplicationById(applicationId: Long): IrkApplicationDTO? {
        return irkService.getApplication(applicationId)
    }

    override val instanceUrl = irkService.serviceUrl

    override fun mapApplicationDtoToApplication(applicationDto: IrkApplicationDTO): Application {
        return irkApplicationMapper.mapApplicationDtoToApplication(applicationDto)
    }

    override fun mapApplicantDtoToApplicant(applicantDto: IrkApplicantDto): Applicant {
        return irkApplicantMapper.mapApplicantDtoToApplicant(applicantDto)
    }

    override fun updateApplication(application: Application, applicationDto: IrkApplicationDTO): Application {
        return irkApplicationMapper.update(application, applicationDto)
    }

    override fun updateApplicant(applicant: Applicant, applicantDto: IrkApplicantDto): Applicant {
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
        val pc = irkService.getPrimaryCertificate(applicationDto.id) ?: return null
        return applicant.educationData.documents.find {
            it.documentNumber == pc.documentNumber && it.certificateTypeCode == pc.certificateTypeCode
        }
    }

    override fun sendNotification(foreignApplicantId: Long, notificationDto: NotificationDto) {
        irkService.sendNotification(foreignApplicantId, notificationDto)
    }

    override fun preprocess(applicationDto: IrkApplicationDTO, applicantDto: IrkApplicantDto) {
    }
}