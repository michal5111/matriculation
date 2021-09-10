package pl.poznan.ue.matriculation.exception

class ImportException(val importId: Long?, message: String?, throwable: Throwable? = null) :
    Exception(message, throwable)