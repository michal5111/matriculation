package pl.poznan.ue.matriculation.excelfile.dto

import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import java.time.LocalDate

data class ExcelFileApplicantDto(
    val id: Long,
    val middle: String?,
    val family: String,
    val given: String,
    val email: String,
    val pesel: String?,
    val passport: String?,
    val issueCountry: String?,
    val issueDate: LocalDate?,
    val birthDate: LocalDate,
    val birthPlace: String,
    val fathersName: String?,
    val mothersName: String?,
    val citizenship: String,
    val address: Address,
    val phoneNumber: String?,
    val sex: Char
) : IApplicantDto {
    override val foreignId: Long
        get() = id
}
