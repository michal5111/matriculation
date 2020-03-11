package pl.ue.poznan.matriculation.irk.mapper

import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component
import pl.ue.poznan.matriculation.irk.dto.applicants.ApplicantDTO
import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant

@Component
class ApplicantMapper {

    private val modelMapper: ModelMapper = ModelMapper()

    fun ApplicantDtoToApplicantMapper(applicantDTO: ApplicantDTO): Applicant? {
        return modelMapper.map(applicantDTO, Applicant::class.java)
    }
}