package pl.poznan.ue.matriculation.dreamApply.dto.applicant

import pl.poznan.ue.matriculation.dreamApply.dto.application.ApplicationDto
import pl.poznan.ue.matriculation.local.dto.AbstractApplicantDto
import java.util.*

data class ApplicantDto(
        val id: Long,
        val registered: Date,
        val name: NameDto,
        val email: String,
        val phone: String,
        val reference: String,
        val citizenship: String,
        val trackers: String,
        val photo: String,
        val documents: String,
        var application: ApplicationDto? = null
) : AbstractApplicantDto() {
    override fun getForeignId(): Long {
        return id
    }
}