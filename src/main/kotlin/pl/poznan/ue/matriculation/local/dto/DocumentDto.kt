package pl.poznan.ue.matriculation.local.dto

import java.time.LocalDate
import java.time.ZonedDateTime

data class DocumentDto(
    val applicantId: Long?,
    val certificateType: String,
    val certificateTypeCode: String,
    val certificateUsosCode: Char?,
    val comment: String?,
    val documentNumber: String?,
    val documentYear: Int?,
    val issueCity: String?,
    val issueCountry: String?,
    val issueDate: LocalDate?,
    val issueInstitution: String?,
    val issueInstitutionUsosCode: Long?,
    val modificationDate: ZonedDateTime?,
)
