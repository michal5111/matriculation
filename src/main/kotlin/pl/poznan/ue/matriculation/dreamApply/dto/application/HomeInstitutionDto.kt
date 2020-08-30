package pl.poznan.ue.matriculation.dreamApply.dto.application

import com.fasterxml.jackson.annotation.JsonProperty

data class HomeInstitutionDto(
        @JsonProperty("name-dropdown")
        val nameDropdown: String?,
        val erasmus: String?,
        val department: HomeDepartmentDto?,
        val country: String?,
        val address: String?
)
