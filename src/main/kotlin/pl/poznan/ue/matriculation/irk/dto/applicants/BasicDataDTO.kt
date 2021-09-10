package pl.poznan.ue.matriculation.irk.dto.applicants

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class BasicDataDTO(
    val sex: Char,

    val pesel: String?,

    @JsonProperty("date_of_birth")
    val dateOfBirth: Date?,

    @JsonProperty("city_of_birth")
    val cityOfBirth: String?,

    @JsonProperty("country_of_birth")
    val countryOfBirth: String,

    @JsonProperty("data_source")
    val dataSource: String
)
