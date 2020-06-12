package pl.poznan.ue.matriculation.irk.dto.applicants

import pl.poznan.ue.matriculation.irk.dto.applications.ApplicationDTO

data class MatriculationDataDTO(
        val application: ApplicationDTO,
        val user: ApplicantDTO
)