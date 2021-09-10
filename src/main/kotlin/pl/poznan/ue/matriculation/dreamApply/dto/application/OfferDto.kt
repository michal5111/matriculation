package pl.poznan.ue.matriculation.dreamApply.dto.application


import com.fasterxml.jackson.annotation.JsonProperty

data class OfferDto(
    val comments: String?,
    @JsonProperty("comments-confirmed")
    val commentsConfirmed: String?,
    val confirmed: String?,
    val course: String?,
    val decision: String?,
    @JsonProperty("decision-deadline")
    val decisionDeadline: String?,
    @JsonProperty("decision-policy")
    val decisionPolicy: String?,
    val id: Int?,
    val inserted: String?,
    val intake: String?,
    val notes: Any?,
    val priority: Any?,
    val saved: String?,
    val score: Score?,
    val scored: String?,
    val type: String?,
    @JsonProperty("type-confirmed")
    val typeConfirmed: String?
)