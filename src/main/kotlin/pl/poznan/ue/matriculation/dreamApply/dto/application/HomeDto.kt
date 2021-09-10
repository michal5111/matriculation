package pl.poznan.ue.matriculation.dreamApply.dto.application

data class HomeDto(
    val course: HomeCourseDto?,
    val institution: HomeInstitutionDto?,
    val contact: HomeContactDto?
)