package pl.poznan.ue.matriculation.irk.dto.programmes

import pl.poznan.ue.matriculation.local.domain.Name


data class ProgrammeGroupsDTO(
    val code: String?,
    val name: Name?,
    val programmes: List<String?>?
)
