package pl.poznan.ue.matriculation.dreamApply.dto.application

data class CareerDto(
        val employer: String?,
        val position: String?,
        val period: PeriodDto?,
        val weekly: String?
) {

}
