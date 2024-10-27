package pl.poznan.ue.matriculation.dreamApply.dto.applicant

import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import java.time.ZonedDateTime

data class DreamApplyApplicantDto(
    val id: Long,
    val registered: ZonedDateTime,
    val name: NameDto,
    val email: String,
    val phone: String?,
    val reference: String?,
    val citizenship: String,
    val trackers: String,
    val photo: String,
    val documents: String,
    var dreamApplyApplication: DreamApplyApplicationDto? = null
) : IApplicantDto {
    override val foreignId: Long
        get() = id
}
