package pl.ue.poznan.matriculation.controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import pl.ue.poznan.matriculation.oracle.repo.PersonRepository
import java.net.URI




@Controller
class IndexController(
        private val personRepository: PersonRepository
) {

//    @GetMapping("/")
//    fun hello(authentication: Authentication?, model: Model): String {
//        if (authentication != null) {
//            val optionalPerson: Optional<Person> = personsRepository.findById(authentication.name.toLong())
//            if (optionalPerson.isPresent) {
//                val user: Person = optionalPerson.get()
//                model.addAttribute("user",user)
//                model.addAttribute("isAuthenticated",authentication.isAuthenticated)
//            }
//        }
//        return "index.html"
//    }

    @GetMapping("/login")
    fun login(@RequestParam service: String): ResponseEntity<String> {
        println(service)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI(service)
        return ResponseEntity(httpHeaders, HttpStatus.MOVED_PERMANENTLY)
    }
}