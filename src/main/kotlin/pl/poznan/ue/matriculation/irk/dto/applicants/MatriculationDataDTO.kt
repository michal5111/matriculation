package pl.poznan.ue.matriculation.irk.dto.applicants

import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.local.dto.IApplicationDto
import java.io.Serializable

data class MatriculationDataDTO(
    val application: IrkApplicationDTO,
    val user: IrkApplicantDto
) : Serializable, IApplicationDto {
    override val foreignApplicantId: Long = user.id
    override val foreignId: Long = application.id
}
