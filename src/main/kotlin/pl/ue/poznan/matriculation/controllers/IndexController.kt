package pl.ue.poznan.matriculation.controllers

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import pl.ue.poznan.matriculation.oracle.domain.Person
import pl.ue.poznan.matriculation.oracle.repo.PersonsRepository
import java.util.*




@Controller
class IndexController(
        private val personsRepository: PersonsRepository
) {

    @GetMapping("/")
    fun hello(authentication: Authentication?, model: Model): String {
        if (authentication != null) {
            val optionalPerson: Optional<Person> = personsRepository.findById(authentication.name.toLong())
            if (optionalPerson.isPresent) {
                val user: Person = optionalPerson.get()
                model.addAttribute("user",user)
                model.addAttribute("isAuthenticated",authentication.isAuthenticated)
            }
        }
        return "index"
    }
}