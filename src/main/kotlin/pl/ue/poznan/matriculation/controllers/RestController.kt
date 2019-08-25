package pl.ue.poznan.matriculation.controllers

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.ue.poznan.matriculation.irk.domain.applicants.Applicant
import pl.ue.poznan.matriculation.irk.domain.applications.Application
import pl.ue.poznan.matriculation.irk.domain.programmes.ProgrammeGroups
import pl.ue.poznan.matriculation.irk.domain.registrations.Registration
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

    @GetMapping("persons")
    fun person(): Person {
        return personsRepository.getOne(SecurityContextHolder.getContext().authentication.name.toLong())
    }

    @GetMapping("persons/{id}")
    fun person(@PathVariable("id") id: Long): Person {
        return personsRepository.getOne(id)
    }

    @GetMapping("applicants/{id}")
    fun applicant(@PathVariable("id") id: Long): Applicant? {
        return irkService.getApplicant(id)
    }

    @GetMapping("registrations/{id}")
    fun getRegistration(@PathVariable("id") id: String): Registration? {
        return irkService.getRegistration(id)
    }

    @GetMapping("applications/{id}")
    fun getApplication(@PathVariable("id") id: Long): Application? {
        return irkService.getApplication(id)
    }

    @GetMapping("programmesGroups/{id}")
    fun getProgrammesGroups(@PathVariable("id") id: String): ProgrammeGroups? {
        return irkService.getProgrammesGroups(id)
    }
}