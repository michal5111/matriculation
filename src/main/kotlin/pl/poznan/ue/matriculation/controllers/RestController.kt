package pl.poznan.ue.matriculation.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.domain.import.ImportProgress
import pl.poznan.ue.matriculation.local.domain.user.Role
import pl.poznan.ue.matriculation.local.domain.user.User
import pl.poznan.ue.matriculation.local.dto.*
import pl.poznan.ue.matriculation.local.job.JobType
import pl.poznan.ue.matriculation.local.service.*
import pl.poznan.ue.matriculation.oracle.dto.IndexTypeDto
import pl.poznan.ue.matriculation.oracle.entityRepresentations.PersonBasicData
import pl.poznan.ue.matriculation.oracle.repo.PersonRepository
import pl.poznan.ue.matriculation.oracle.service.UsosService

@RestController
@RequestMapping("/api")
@CrossOrigin(
    originPatterns = ["*"],
    allowCredentials = "true",
    methods = [RequestMethod.GET, RequestMethod.PATCH, RequestMethod.TRACE, RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.PUT]
)
class RestController(
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val importService: ImportService,
    private val usosService: UsosService,
    private val applicationService: ApplicationService,
    private val userService: UserService,
    private val roleService: RoleService,
    private val jobService: JobService,
    private val personRepository: PersonRepository,
    private val applicantService: ApplicantService
) {

    @Value("\${pl.poznan.ue.matriculation.usos.url}")
    private lateinit var usosUrl: String

    @GetMapping("/user")
    fun user(): Any {
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if (principal is String) {
            "{}"
        } else principal
    }

    @GetMapping("/registrations/{dataSourceType}/codes")
    fun getRegistrationCodes(
        @PathVariable("dataSourceType") dataSourceType: String
    ): List<RegistrationDto> = applicationDataSourceFactory
        .getDataSource(dataSourceType)
        .getAvailableRegistrations()

    @GetMapping("/registrations/{dataSourceType}/codes/{id}")
    fun getProgrammesCodes(
        @PathVariable("id") id: String,
        @PathVariable("dataSourceType") dataSourceType: String
    ): List<ProgrammeDto> = applicationDataSourceFactory
        .getDataSource(dataSourceType)
        .getAvailableRegistrationProgrammes(id)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/import")
    fun createImport(
        @RequestBody importDto: ImportDto
    ): Import = importService.create(
        programmeCode = importDto.programmeCode,
        programmeForeignId = importDto.programmeForeignId,
        registration = importDto.registration,
        indexPoolCode = importDto.indexPoolCode,
        startDate = importDto.startDate,
        dateOfAddmision = importDto.dateOfAddmision,
        stageCode = importDto.stageCode,
        didacticCycleCode = importDto.didacticCycleCode,
        dataSourceType = importDto.dataSourceId,
        dataFile = importDto.dataFile
    )

    @PutMapping("/import/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun importApplicants(@PathVariable("id") importId: Long) = jobService.runJob(JobType.IMPORT, importId)

    @GetMapping("/import/{id}")
    fun getImport(@PathVariable("id") importId: Long): Import = importService.get(importId)


    @GetMapping("/import/{id}/progress")
    fun getProgress(@PathVariable("id") importId: Long): ImportProgress = importService.getProgress(importId)

    @GetMapping("/import/{id}/save")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun savePersons(@PathVariable("id") importId: Long) = jobService.runJob(JobType.SAVE, importId)

    @GetMapping("/import")
    fun getImportsPage(pageable: Pageable): Page<Import> = importService.getAll(pageable)


    @DeleteMapping("/import/{id}")
    fun deleteImport(@PathVariable("id") importId: Long) = importService.delete(importId)


    @GetMapping("/import/{id}/applications")
    fun findAllApplicationsByImportId(
        pageable: Pageable,
        @PathVariable("id") importId: Long
    ): Page<Application> = applicationService.findAllApplicationsByImportId(pageable, importId)


    @GetMapping("/usos/indexPool")
    fun getAvailableIndexPools(): List<IndexTypeDto> = usosService.getAvailableIndexPoolsCodes()


    @GetMapping("/usos/programme/{code}/stages")
    fun getAvailableStages(@PathVariable("code") code: String): List<String> = usosService.getStageByProgrammeCode(code)


    @GetMapping("/usos/didacticCycle")
    fun findDidacticCycleByCode(@RequestParam("code") didacticCycleCode: String): List<String> =
        usosService.findDidacticCycleCodes(didacticCycleCode, 10)


    @PutMapping("/usos/person/{id}/indexNumber")
    fun updateIndexNumberByUsosIdAndIndexType(
        @PathVariable("id") personId: Long,
        @RequestParam("indexType") indexTypeCode: String,
        @RequestParam("indexNumber") indexNumber: String
    ) = usosService.updateIndexNumberByUsosIdAndIndexType(personId, indexTypeCode, indexNumber)

    @PutMapping("/import/{id}/archive")
    fun archiveImport(@PathVariable("id") importId: Long) = jobService.runJob(JobType.ARCHIVE, importId)

    @GetMapping("/import/dataSources")
    fun getDataSources(): List<DataSourceDto> = applicationDataSourceFactory.getDataSources()

    @GetMapping("/usos/url")
    fun getUsosUrl(): UrlDto = UrlDto(usosUrl)

    @PostMapping("/user")
    fun createUser(@RequestBody userDto: UserDto): User = userService.save(userDto)

    @PutMapping("/user")
    fun updateUser(@RequestBody userDto: UserDto): User = userService.update(userDto)

    @DeleteMapping("/user/{id}")
    fun deleteUser(@PathVariable("id") id: Long) = userService.delete(id)

    @GetMapping("/users")
    fun getAllUsers(pageable: Pageable): Page<User> = userService.getAll(pageable)

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
        val applicant = applicantService.findById(applicantId) ?: throw ApplicantNotFoundException()
        return personRepository.findPotentialDuplicate(
            name = applicant.name.given,
            surname = applicant.name.family,
            applicant.basicData.dateOfBirth!!,
            applicant.identityDocuments.map {
                it.number!!
            }
        )
    }

    @GetMapping("/role")
    fun getAllRoles(): List<Role> = roleService.getAll()

    @GetMapping("/import/{id}/job/{jobType}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun runJob(@PathVariable("id") importId: Long, @PathVariable("jobType") jobType: JobType) =
        jobService.runJob(jobType, importId)

    @PutMapping("/application/{id}/potentialDuplicateStatus")
    fun updatePotentialDuplicateStatus(
        @PathVariable("id") applicationId: Long,
        @RequestBody potentialDuplicateStatusDto: ApplicantUsosIdAndPotentialDuplicateStatusDto
    ): Application {
        return applicationService.updatePotentialDuplicateStatus(applicationId, potentialDuplicateStatusDto)
    }
}
