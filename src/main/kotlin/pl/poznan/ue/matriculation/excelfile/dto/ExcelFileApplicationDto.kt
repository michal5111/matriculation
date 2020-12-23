package pl.poznan.ue.matriculation.excelfile.dto

import pl.poznan.ue.matriculation.local.dto.AbstractApplicationDto

data class ExcelFileApplicationDto(
    val id: Long,
    val applicant: ExcelFileApplicantDto
) : AbstractApplicationDto() {

    override fun getForeignApplicantId(): Long {
        return applicant.getForeignId()
    }

    override fun getForeignId(): Long {
        return id
    }
}