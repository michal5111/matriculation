package pl.ue.poznan.matriculation.local.domain.programmes

import pl.ue.poznan.matriculation.local.domain.Name
import javax.persistence.Id

data class ProgrammeGroups(

        @Id
        val code: String,
        val name: Name?,
        val programmes: List<String?>?
)