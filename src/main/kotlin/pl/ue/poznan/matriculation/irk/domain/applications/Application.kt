package pl.ue.poznan.matriculation.irk.domain.applications


import com.fasterxml.jackson.annotation.JsonProperty
import pl.ue.poznan.matriculation.irk.dto.applications.ForeignerDataDTO
import pl.ue.poznan.matriculation.local.domain.Turn
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant

data class Application(
        val admitted: String?,
        val comment: String?,
        @JsonProperty("foreigner_data")
    val foreignerDataDTO: ForeignerDataDTO?,
        val id: Long,
        val payment: String?,
        val position: String?,
        val qualified: String?,
        val score: String?,
        val turnDTO: Turn?,
        var user: Applicant?
)