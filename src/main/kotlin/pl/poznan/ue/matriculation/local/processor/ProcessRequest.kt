package pl.poznan.ue.matriculation.local.processor

import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.oracle.domain.Person

data class ProcessRequest(
    val application: Application,
    val import: Import,
    var person: Person? = null
)
