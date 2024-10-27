package pl.poznan.ue.matriculation.dreamApply.dto.application

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class PassportDto(
    val number: String?,
    val issue: String?,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val expiry: LocalDate?,
    val country: String?,
    val issuer: String?,
    val series: String?,
    val idcard: String?
)
