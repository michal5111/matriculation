package pl.poznan.ue.matriculation.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Datasource not found.")
class DataSourceNotFoundException : Exception()