package pl.poznan.ue.matriculation.dreamApply.dto.application

import com.fasterxml.jackson.annotation.JsonProperty

data class LanguagesDto(
        @JsonProperty("0")
        val firstLanguage: LanguageDto?,
        @JsonProperty("1")
        val secondLanguage: LanguageDto?,
        @JsonProperty("2")
        val thirdLanguage: LanguageDto?,
        val native: String
) {
}
