package pl.poznan.ue.matriculation.local.domain.programmes

import jakarta.persistence.Id
import pl.poznan.ue.matriculation.local.domain.Name

open class ProgrammeGroups(

    @Id
    val code: String,
    val name: Name?,
    val programmes: List<String?>?
)
