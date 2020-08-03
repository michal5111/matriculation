package pl.poznan.ue.matriculation.dreamApply.dto.application

data class PassportDto(
        val number: String?,
        val issue: String?,
        val expiry: String?,
        val country: String?,
        val issuer: String?,
        val series: String?,
        val idcard: String?
)
