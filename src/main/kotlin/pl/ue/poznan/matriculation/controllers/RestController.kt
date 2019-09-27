package pl.ue.poznan.matriculation.controllers

import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.ue.poznan.matriculation.irk.domain.Page
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant
import pl.ue.poznan.matriculation.local.domain.applications.Application
import pl.ue.poznan.matriculation.local.domain.programmes.ProgrammeGroups
import pl.ue.poznan.matriculation.local.domain.registrations.Registration
import pl.ue.poznan.matriculation.irk.service.IrkService
import pl.ue.poznan.matriculation.oracle.domain.Person
import pl.ue.poznan.matriculation.oracle.repo.PersonsRepository

@RestController
@RequestMapping("/api")
class RestController(
        private val personsRepository: PersonsRepository,
        private val irkService: IrkService
) {
    @GetMapping("/user")
    fun user(): Any {
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if (principal is String) {
            "{}"
        } else principal
    }

    @GetMapping("/test")
    fun test(): String {
        return "test"
    }

    @GetMapping("person")
    fun person(): Person {
        return personsRepository.getOne(SecurityContextHolder.getContext().authentication.name.toLong())
    }

    @GetMapping("persons")
    fun person(pageable: Pageable): org.springframework.data.domain.Page<Person> {
        return personsRepository.findAll(pageable)
    }

    @GetMapping("persons/{id}")
    fun person(@PathVariable("id") id: Long): Person {
        return personsRepository.getOne(id)
    }

    @GetMapping("applicants/{id}")
    fun getApplicantById(@PathVariable("id") id: Long): Applicant? {
        return irkService.getApplicantById(id)
    }

    @GetMapping("applicants/")
    fun getApplicantByParam(
            @RequestParam(required = false) pesel: String?,
            @RequestParam(required = false) surname: String?,
            @RequestParam(required = false) email: String?
    ): Page<Applicant>? {
        if (pesel != null) return irkService.getApplicantsByPesel(pesel)
        if (surname != null) return irkService.getApplicantsBySurname(surname)
        if (email != null) return irkService.getApplicantsByEmail(email)
        throw IllegalArgumentException()
    }

    @GetMapping("registrations/{id}")
    fun getRegistration(@PathVariable("id") id: String): Registration? {
        return irkService.getRegistration(id)
    }

    @GetMapping("applications/{id}")
    fun getApplication(@PathVariable("id") id: Long): Application? {
        return irkService.getApplication(id)
    }

    @GetMapping("applications")
    fun getApplications(
            @RequestParam(required = false) qualified: Boolean,
            @RequestParam(required = false) paid: Boolean,
            @RequestParam(required = false) pageNumber: Int?
    ): Page<Application>? {
        return irkService.getApplications(qualified, paid, pageNumber)
    }

    @GetMapping("programmesGroups/{id}")
    fun getProgrammesGroups(@PathVariable("id") id: String): ProgrammeGroups? {
        return irkService.getProgrammesGroups(id)
    }
}