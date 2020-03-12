package pl.ue.poznan.matriculation.controllers

import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.ue.poznan.matriculation.irk.dto.applicants.ApplicantDTO
import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
import pl.ue.poznan.matriculation.irk.dto.programmes.ProgrammeGroupsDTO
import pl.ue.poznan.matriculation.irk.dto.registrations.RegistrationDTO
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
    fun getApplicantById(@PathVariable("id") id: Long): ApplicantDTO? {
        return irkService.getApplicantById(id)
    }

    @GetMapping("applicants/")
    fun getApplicantByParam(
            @RequestParam(required = false) pesel: String?,
            @RequestParam(required = false) surname: String?,
            @RequestParam(required = false) email: String?
    ): pl.ue.poznan.matriculation.irk.domain.Page<ApplicantDTO>? {
        if (pesel != null) return irkService.getApplicantsByPesel(pesel)
        if (surname != null) return irkService.getApplicantsBySurname(surname)
        if (email != null) return irkService.getApplicantsByEmail(email)
        throw IllegalArgumentException()
    }

    @GetMapping("registrations/{id}")
    fun getRegistration(@PathVariable("id") id: String): RegistrationDTO? {
        return irkService.getRegistration(id)
    }

    @GetMapping("applications/{id}")
    fun getApplication(@PathVariable("id") id: Long): ApplicationDTO? {
        return irkService.getApplication(id)
    }

    @GetMapping("applications")
    fun getApplications(
        @RequestParam(required = false) admitted: Boolean,
        @RequestParam(required = false) paid: Boolean,
        @RequestParam(required = false) programme: String?,
        @RequestParam(required = false) registration: String?,
        @RequestParam(required = false) pageNumber: Int?
    ): pl.ue.poznan.matriculation.irk.domain.Page<ApplicationDTO>? {
        return irkService.getApplications(admitted, paid, registration, programme, pageNumber)
    }

    @GetMapping("programmesGroups/{id}")
    fun getProgrammesGroups(@PathVariable("id") id: String): ProgrammeGroupsDTO? {
        return irkService.getProgrammesGroups(id)
    }
}
