package pl.ue.poznan.matriculation.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ImportStartException(val importId: Long, messageString: String): Exception(messageString)