package pl.poznan.ue.matriculation.dreamApply.dto.application

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.NameDto

data class ProfileDto(
    val name: NameDto?,
    val id: Long?,
    val passport: PassportDto?,
    val birth: BirthDto?,
    val nationality: String?,
    val citizenship: String?,
    val gender: String?,
    val maritial: Char?,
    val family: FamilyDto?,
    val nationalidcode: NationalCodeDto?
)