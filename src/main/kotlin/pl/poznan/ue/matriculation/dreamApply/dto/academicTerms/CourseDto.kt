package pl.poznan.ue.matriculation.dreamApply.dto.academicTerms

import java.util.*

data class CourseDto(
        val id: Long,
        val status: String,
        val updated: Date,
        val institution: String,
        //val intakes: Map<Long, String>,
        val featured: String,
        val type: String,
        val name: String,
        val mode: String,
        val duration: String?,
        val credits: String?,
        val language: String,
        val country: String,
        val location: String,
        val code: String?,
        val quota: String?
) {

}