package pl.poznan.ue.matriculation.exception.exceptionHandler

interface ISaveExceptionHandler {
    fun handle(exception: Exception, applicationId: Long, importId: Long)
}
