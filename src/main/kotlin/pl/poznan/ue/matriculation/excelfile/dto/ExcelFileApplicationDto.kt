package pl.poznan.ue.matriculation.excelfile.dto

import pl.poznan.ue.matriculation.local.dto.IApplicationDto

data class ExcelFileApplicationDto(
    val id: Long,
    val applicant: ExcelFileApplicantDto
) : IApplicationDto {

    override val foreignApplicantId: Long
        get() = applicant.foreignId

    override val foreignId: Long
        get() = id
}