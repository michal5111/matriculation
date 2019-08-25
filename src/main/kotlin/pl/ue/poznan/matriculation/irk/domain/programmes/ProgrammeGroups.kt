package pl.ue.poznan.matriculation.irk.domain.programmes

import pl.ue.poznan.matriculation.irk.domain.Name


data class ProgrammeGroups(
        val code: String?,
        val name: Name?,
        val programmes: List<String?>?
)