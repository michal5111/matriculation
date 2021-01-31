package pl.poznan.ue.matriculation.exception.exceptionHandler

import pl.poznan.ue.matriculation.local.domain.applications.Application

interface ISaveExceptionHandler {
    fun handle(exception: Exception, application: Application, importId: Long)
}