package pl.poznan.ue.matriculation.irk.dto.applicants

import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO

data class MatriculationDataDTO(
        val application: IrkApplicationDTO,
        val user: IrkApplicantDto
)