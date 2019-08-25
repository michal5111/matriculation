package pl.ue.poznan.matriculation.irk.domain.applicants


import com.fasterxml.jackson.annotation.JsonProperty

data class AdditionalData(
    @JsonProperty("city_of_birth")
    val cityOfBirth: String?,
    @JsonProperty("country_of_birth")
    val countryOfBirth: String?,
    @JsonProperty("document_country")
    val documentCountry: String?,
    @JsonProperty("document_exp_date")
    val documentExpDate: String?,
    @JsonProperty("document_number")
    val documentNumber: String?,
    @JsonProperty("document_type")
    val documentType: String?,
    @JsonProperty("fathers_name")
    val fathersName: String?,
    @JsonProperty("military_category")
    val militaryCategory: String?,
    @JsonProperty("military_status")
    val militaryStatus: String?,
    @JsonProperty("mothers_name")
    val mothersName: String?,
    val wku: String?
)