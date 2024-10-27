package pl.poznan.ue.matriculation.dreamApply.dto.application

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class BirthDto(

    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate?,
    val place: String?,
    val country: String?
)
