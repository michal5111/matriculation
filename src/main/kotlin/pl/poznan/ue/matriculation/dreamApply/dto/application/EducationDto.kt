package pl.poznan.ue.matriculation.dreamApply.dto.application

data class EducationDto(
        val level: String?,
        val course: String?,
        val yearAndMounth: YearAndMounthDto?,
        val institution: String?,
        val country: String?
) {

}
