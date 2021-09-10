package pl.poznan.ue.matriculation.dreamApply.dto.application

data class EducationDto(
    val level: EducationLevelType?,
    val course: String?,
    val yearAndMonth: YearAndMounthDto?,
    val institution: String?,
    val country: String?,
    val location: String?,
    val city: String?,
    val diploma: DiplomaDto?
)
