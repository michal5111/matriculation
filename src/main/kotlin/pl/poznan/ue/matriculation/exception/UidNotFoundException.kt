package pl.poznan.ue.matriculation.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Uid nie znaleziony")
class UidNotFoundException : Exception()