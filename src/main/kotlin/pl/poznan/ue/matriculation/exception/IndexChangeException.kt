package pl.poznan.ue.matriculation.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class IndexChangeException(message: String?, exception: java.lang.Exception): Exception(message, exception) {
}