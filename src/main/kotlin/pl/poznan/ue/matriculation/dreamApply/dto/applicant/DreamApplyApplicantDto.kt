package pl.poznan.ue.matriculation.dreamApply.dto.applicant

import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.local.dto.AbstractApplicantDto
import java.util.*

data class DreamApplyApplicantDto(
        val id: Long,
        val registered: Date,
        val name: NameDto,
        val email: String,
        val phone: String?,
        val reference: String?,
        val citizenship: String,
        val trackers: String,
        val photo: String,
        val documents: String,
        var dreamApplyApplication: DreamApplyApplicationDto? = null
) : AbstractApplicantDto() {
    override fun getForeignId(): Long {
        return id
    }
}