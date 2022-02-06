package pl.poznan.ue.matriculation.controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URI


@Controller
class AuthController {

    @GetMapping("/login")
    fun login(@RequestParam service: String): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI(service)
        return ResponseEntity(httpHeaders, HttpStatus.MOVED_PERMANENTLY)
    }
}
