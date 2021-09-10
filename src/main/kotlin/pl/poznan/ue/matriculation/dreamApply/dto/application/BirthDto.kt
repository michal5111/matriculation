package pl.poznan.ue.matriculation.dreamApply.dto.application

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class BirthDto(

    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: Date?,
    val place: String?,
    val country: String?
)
