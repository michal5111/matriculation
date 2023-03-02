package pl.poznan.ue.matriculation.controllers

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.local.domain.user.Role
import pl.poznan.ue.matriculation.local.dto.*
import pl.poznan.ue.matriculation.local.job.JobType
import pl.poznan.ue.matriculation.local.service.*
import pl.poznan.ue.matriculation.oracle.entityRepresentations.PersonBasicData
import pl.poznan.ue.matriculation.oracle.repo.PersonRepository

@RestController
@RequestMapping("/api")
class RestController(
    private val roleService: RoleService,
    private val jobService: JobService,
    private val personRepository: PersonRepository,
    private val applicantService: ApplicantService
) {

    @GetMapping("/user")
    fun user(): Any {
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if (principal is String) "{}" else principal
    }

    @PutMapping("/import/{id}/archive")
    fun archiveImport(@PathVariable("id") importId: Long) = jobService.runJob(JobType.ARCHIVE, importId)

    @GetMapping("/import/{id}/importUids")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getUids(@PathVariable("id") importId: Long) = jobService.runJob(JobType.FIND_UIDS, importId)

    @GetMapping("/import/{id}/notifications")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun sendNotifications(@PathVariable("id") importId: Long) = jobService.runJob(JobType.SEND_NOTIFICATIONS, importId)

    @GetMapping("/import/{id}/checkForDuplicates")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun checkForDuplicates(@PathVariable("id") importId: Long) =
        jobService.runJob(JobType.CHECK_FOR_POTENTIAL_DUPLICATES, importId)

    @GetMapping("/applicant/{id}/potentialDuplicates")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getPotentialDuplicates(@PathVariable("id") applicantId: Long): List<PersonBasicData> {
        val applicant =
            applicantService.findWithIdentityDocumentsById(applicantId) ?: throw ApplicantNotFoundException()
        return personRepository.findPotentialDuplicate(
            name = applicant.given,
            surname = applicant.family,
            birthDate = applicant.dateOfBirth!!,
            idNumbers = applicant.identityDocuments.map {
                it.number!!
            },
            privateEmail = applicant.email,
            email = applicant.email
        )
    }

    @GetMapping("/role")
    fun getAllRoles(): List<Role> = roleService.getAll()

    @GetMapping("/import/{id}/job/{jobType}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun runJob(@PathVariable("id") importId: Long, @PathVariable("jobType") jobType: JobType) =
        jobService.runJob(jobType, importId)
}
