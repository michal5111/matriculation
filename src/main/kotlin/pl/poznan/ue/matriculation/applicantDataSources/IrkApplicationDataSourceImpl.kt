package pl.poznan.ue.matriculation.applicantDataSources

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.client.HttpStatusCodeException
import pl.poznan.ue.matriculation.irk.dto.ErrorMessageDto
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicantMapper
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicationMapper
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto
import pl.poznan.ue.matriculation.oracle.domain.IrkApplication

class IrkApplicationDataSourceImpl(
        private val irkService: IrkService,
        private val name: String,
        private val id: String,
        private val setAsAccepted: Boolean
) : IApplicationDataSource<IrkApplicationDTO, IrkApplicantDto> {

    private val irkApplicationMapper = IrkApplicationMapper()
    private val irkApplicantMapper = IrkApplicantMapper()

    override fun getApplicationsPage(registrationCode: String, programmeForeignId: String, pageNumber: Int): IPage<IrkApplicationDTO> {
        val page = irkService.getApplications(
                admitted = true,
                paid = true,
                registration = registrationCode,
                programme = programmeForeignId,
                pageNumber = pageNumber
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

    override fun getName(): String {
        return name
    }

    override fun getId(): String {
        return id
    }

    override fun postMatriculation(applicationId: Long, irkApplication: IrkApplication) {
        if (!setAsAccepted) return
        try {
            irkService.completeImmatriculation(applicationId)
            irkApplication.confirmationStatus = 1
        } catch (e: HttpStatusCodeException) {
            val errorMessageDto = jacksonObjectMapper().readValue(e.responseBodyAsString, ErrorMessageDto::class.java)
            irkApplication.confirmationStatus = errorMessageDto.error
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

//    override fun getRegistrationByCode(registration: String): Registration? {
//        return irkService.getRegistration(registration)?.let {
//            Registration(
//                    code = it.code,
//                    cycle = it.cycle,
//                    employeesOnly = it.employeesOnly,
//                    name = it.name,
//                    programmes = it.programmes,
//                    status = it.status,
//                    turnDTOS = it.turn,
//                    tag = it.tag
//            )
//        }
//    }

    override fun getApplicationById(applicationId: Long): IrkApplicationDTO? {
        return irkService.getApplication(applicationId)
    }

    override fun getInstanceUrl(): String {
        return irkService.serviceUrl
    }

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

    override fun preprocess(applicationDto: IrkApplicationDTO, applicantDto: IrkApplicantDto) {
    }

    override fun getPrimaryCertificate(applicationId: Long, documents: List<Document>): Document? {
        return documents.find {
            irkService.getPrimaryCertificate(applicationId)?.let { primaryCertificate ->
                return@let it.documentNumber == primaryCertificate.documentNumber &&
                        it.certificateTypeCode == primaryCertificate.certificateTypeCode
            } ?: false
        }
    }
}