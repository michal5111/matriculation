package pl.poznan.ue.matriculation.dreamApply.dto.application

data class ResidenceDto(
        val country: String,
        val purpose: String,
        val period: PeriodDto
) {

}
