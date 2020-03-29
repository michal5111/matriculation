package pl.ue.poznan.matriculation.exception

class ImportException(val importId: Long, message: String?, throwable: Throwable? = null): Exception(message, throwable)