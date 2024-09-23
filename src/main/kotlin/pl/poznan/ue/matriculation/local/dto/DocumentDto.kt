package pl.poznan.ue.matriculation.local.dto

import java.util.*

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
    val issueDate: Date?,
    val issueInstitution: String?,
    val issueInstitutionUsosCode: Long?,
    val modificationDate: Date?,
)
