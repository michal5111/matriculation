package pl.ue.poznan.matriculation.irk.dto.programmes

import pl.ue.poznan.matriculation.local.domain.Name


data class ProgrammeGroupsDTO(
        val code: String?,
        val name: Name?,
        val programmes: List<String?>?
)