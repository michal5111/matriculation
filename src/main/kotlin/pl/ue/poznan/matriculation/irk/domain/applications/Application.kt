package pl.ue.poznan.matriculation.irk.domain.applications


import com.fasterxml.jackson.annotation.JsonProperty

data class Application(
    val admitted: String?,
    val comment: String?,
    @JsonProperty("foreigner_data")
    val foreignerData: ForeignerData?,
    val id: Long,
    val payment: String?,
    val position: String?,
    val qualified: String?,
    val score: String?,
    val turn: Turn?,
    val user: Long?
)