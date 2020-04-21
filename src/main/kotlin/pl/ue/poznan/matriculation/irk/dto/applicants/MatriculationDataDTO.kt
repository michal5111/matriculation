package pl.ue.poznan.matriculation.irk.dto.applicants

import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO

data class MatriculationDataDTO(
        val applicationDTO: ApplicationDTO,
        val applicantDTO: ApplicantDTO
)