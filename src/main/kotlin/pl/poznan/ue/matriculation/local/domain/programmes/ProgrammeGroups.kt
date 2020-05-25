package pl.poznan.ue.matriculation.local.domain.programmes

import pl.poznan.ue.matriculation.local.domain.Name
import javax.persistence.Id

class ProgrammeGroups(

        @Id
        val code: String,
        val name: Name?,
        val programmes: List<String?>?
)