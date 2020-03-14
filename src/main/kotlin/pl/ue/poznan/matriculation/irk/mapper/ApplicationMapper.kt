package pl.ue.poznan.matriculation.irk.mapper

import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component
import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
import pl.ue.poznan.matriculation.irk.service.IrkService
import pl.ue.poznan.matriculation.irk.domain.applications.Application

@Component
class ApplicationMapper(
        private val irkService: IrkService,
        private val applicantMapper: ApplicantMapper
) {

    private val modelMapper: ModelMapper = ModelMapper()

    fun applicationDtoToApplicationMapper(applicationDTO: ApplicationDTO): Application {
        val application = modelMapper.map(applicationDTO, Application::class.java)
        val applicantDTO = irkService.getApplicantById(applicationDTO.user)
        val applicant = applicantDTO?.let { applicantMapper.applicantDtoToApplicantMapper(it) }
        application.user = applicant
        return application
    }
}