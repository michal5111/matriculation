package pl.ue.poznan.matriculation.irk.domain.applicants


import com.fasterxml.jackson.annotation.JsonProperty

data class ContactData(
    @JsonProperty("modification_date")
    val modificationDate: String?,
    @JsonProperty("official_city")
    val officialCity: String?,
    @JsonProperty("official_city_is_city")
    val officialCityIsCity: Boolean,
    @JsonProperty("official_country")
    val officialCountry: String?,
    @JsonProperty("official_flat_number")
    val officialFlatNumber: String?,
    @JsonProperty("official_post_code")
    val officialPostCode: String?,
    @JsonProperty("official_street")
    val officialStreet: Any?,
    @JsonProperty("official_street_number")
    val officialStreetNumber: String,
    @JsonProperty("phone_number")
    val phoneNumber: String?,
    @JsonProperty("phone_number2")
    val phoneNumber2: String?,
    @JsonProperty("phone_number2_type")
    val phoneNumber2Type: String?,
    @JsonProperty("phone_number_type")
    val phoneNumberType: String?,
    @JsonProperty("real_city")
    val realCity: String?,
    @JsonProperty("real_city_is_city")
    val realCityIsCity: Boolean,
    @JsonProperty("real_country")
    val realCountry: String?,
    @JsonProperty("real_flat_number")
    val realFlatNumber: String?,
    @JsonProperty("real_post_code")
    val realPostCode: String?,
    @JsonProperty("real_street")
    val realStreet: String?,
    @JsonProperty("real_street_number")
    val realStreetNumber: String?
)