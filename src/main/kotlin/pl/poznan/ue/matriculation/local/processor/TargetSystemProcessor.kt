package pl.poznan.ue.matriculation.local.processor

import pl.poznan.ue.matriculation.local.dto.ProcessResult

interface TargetSystemProcessor<T> {
    fun process(processRequest: ProcessRequest): ProcessResult<T>
}
