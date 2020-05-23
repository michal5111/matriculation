package pl.poznan.ue.matriculation.irk.dto.applicants


import com.fasterxml.jackson.annotation.JsonProperty

data class ForeignerDataDTO(
        @JsonProperty("base_of_stay")
    val baseOfStay: String?,
        @JsonProperty("foreigner_status")
    val foreignerStatus: List<String>,
        @JsonProperty("polish_card_issue_country")
    val polishCardIssueCountry: String?,
        @JsonProperty("polish_card_issue_date")
    val polishCardIssueDate: String?,
        @JsonProperty("polish_card_number")
    val polishCardNumber: String?,
        @JsonProperty("polish_card_valid_to")
    val polishCardValidTo: String?
)