package pl.poznan.ue.matriculation.controllers

import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class CsrfController {

    @GetMapping("/api/csrf")
    fun csrf(csrfToken: CsrfToken): CsrfToken {
        return csrfToken
    }
}
