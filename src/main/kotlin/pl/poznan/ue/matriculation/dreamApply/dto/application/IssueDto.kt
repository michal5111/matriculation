package pl.poznan.ue.matriculation.dreamApply.dto.application

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class IssueDto(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: Date?
)
