package pl.poznan.ue.matriculation.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ImportNotFoundException(message: String = "Import not found") : Exception(message)