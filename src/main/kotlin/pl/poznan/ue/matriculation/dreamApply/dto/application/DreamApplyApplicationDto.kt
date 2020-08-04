package pl.poznan.ue.matriculation.dreamApply.dto.application

import com.fasterxml.jackson.annotation.JsonProperty
import pl.poznan.ue.matriculation.local.dto.AbstractApplicationDto
import java.util.*

data class DreamApplyApplicationDto(
        var id: Long,
        val created: Date,
        val revised: Date,
        val submitted: String?,
        val status: String,
        @JsonProperty("academic_term")
        val academicTerm: String,
        val flags: String,
        val courses: String,
        val offers: String,
        val exports: String,
        val documents: String,
        val studyplans: String,
        val applicant: String,
        val profile: ProfileDto?,
        val contact: ContactDto?,
        val education: List<EducationDto>?,
        //val languages: LanguagesDto?,
        val career: List<CareerDto>?,
        val activities: List<ActivityDto>?,
        val residences: List<ResidenceDto>?,
        //val motivation: List<MotivationDto>?,
        val misc: MiscDto?,
        val extras: List<ExtraDto>?
) : AbstractApplicationDto() {
    override fun getForeignApplicantId(): Long {
        return applicant.substring(16, applicant.length).toLong()
    }

    override fun getForeignId(): Long {
        return id
    }

}