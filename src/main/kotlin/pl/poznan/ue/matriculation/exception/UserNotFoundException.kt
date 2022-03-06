package pl.poznan.ue.matriculation.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Użytkownik nie znaleziony")
class UserNotFoundException() : Exception() {
}
