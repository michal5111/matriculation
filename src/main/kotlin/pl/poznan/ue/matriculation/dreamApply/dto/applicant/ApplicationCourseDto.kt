package pl.poznan.ue.matriculation.dreamApply.dto.applicant

import java.time.LocalDate

data class ApplicationCourseDto(
    val priority: Int?,
    val submitted: LocalDate?,
    val course: String?,
    val intake: String?,
    val modifier: String?
)
