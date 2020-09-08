package pl.poznan.ue.matriculation.dreamApply.dto.applicant

import java.util.*

data class ApplicationCourseDto(
        val priority: Int?,
        val submitted: Date?,
        val course: String?,
        val intake: String?,
        val modifier: String?
)