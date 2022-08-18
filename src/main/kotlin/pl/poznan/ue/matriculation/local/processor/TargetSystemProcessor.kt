package pl.poznan.ue.matriculation.local.processor

import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ProcessResult

interface TargetSystemProcessor<T> {
    fun process(application: Application, import: Import, person: T): ProcessResult<T>
}
