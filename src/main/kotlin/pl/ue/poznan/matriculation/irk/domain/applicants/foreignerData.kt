package pl.ue.poznan.matriculation.irk.domain.applicants


import com.fasterxml.jackson.annotation.JsonProperty

data class foreignerData(
    @JsonProperty("base_of_stay")
    val baseOfStay: String?,
    @JsonProperty("foreigner_status")
    val foreignerStatus: List<Any>,
    @JsonProperty("polish_card_issue_country")
    val polishCardIssueCountry: String?,
    @JsonProperty("polish_card_issue_date")
    val polishCardIssueDate: String?,
    @JsonProperty("polish_card_number")
    val polishCardNumber: String?,
    @JsonProperty("polish_card_valid_to")
    val polishCardValidTo: String?
)