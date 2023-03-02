package pl.poznan.ue.matriculation.dreamApply.dto.application

import com.fasterxml.jackson.annotation.JsonProperty
import pl.poznan.ue.matriculation.dreamApply.dto.academicTerms.CourseDto
import pl.poznan.ue.matriculation.local.dto.IApplicationDto
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
    //val misc: List<MiscDto>?,
    val extras: List<ExtraDto>?,
    val home: HomeDto?,
    val coursesDto: List<CourseDto>
) : IApplicationDto {

    override val foreignApplicantId: Long
        get() = applicant.substring(16, applicant.length).toLong()

    override val foreignId: Long
        get() = id

}
