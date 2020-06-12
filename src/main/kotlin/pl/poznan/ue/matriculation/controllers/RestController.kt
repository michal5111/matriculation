package pl.poznan.ue.matriculation.controllers

import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.poznan.ue.matriculation.irk.dto.Page
import pl.poznan.ue.matriculation.irk.dto.applicants.ApplicantDTO
import pl.poznan.ue.matriculation.irk.dto.applications.ApplicationDTO
import pl.poznan.ue.matriculation.irk.dto.programmes.ProgrammeGroupsDTO
import pl.poznan.ue.matriculation.irk.dto.registrations.RegistrationDTO
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.domain.import.ImportProgress
import pl.poznan.ue.matriculation.local.dto.ImportDto
import pl.poznan.ue.matriculation.local.service.ApplicationService
import pl.poznan.ue.matriculation.local.service.AsyncService
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.oracle.dto.IndexTypeDto
import pl.poznan.ue.matriculation.oracle.service.UsosService

@RestController
@RequestMapping("/api")
class RestController(
        private val irkService: IrkService,
        private val importService: ImportService,
        private val asyncService: AsyncService,
        private val usosService: UsosService,
        private val applicationService: ApplicationService
) {
    @GetMapping("/user")
    fun user(): Any {
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if (principal is String) {
            "{}"
        } else principal
    }

//    @GetMapping("/person")
//    fun person(): Person {
//        return personRepository.getOne(SecurityContextHolder.getContext().authentication.name.toLong())
//    }
//
//    @GetMapping("/persons")
//    fun person(pageable: Pageable): org.springframework.data.domain.Page<Person> {
//        return personRepository.findAll(pageable)
//    }
//
//    @GetMapping("/persons/{id}")
//    fun person(@PathVariable("id") id: Long): Person {
//        return personRepository.getOne(id)
//    }

    @GetMapping("/applicants/{id}")
    fun getApplicantById(@PathVariable("id") id: Long): ApplicantDTO? = irkService.getApplicantById(id)

    @GetMapping("/applicants/")
    fun getApplicantByParam(
            @RequestParam(required = false) pesel: String?,
            @RequestParam(required = false) surname: String?,
            @RequestParam(required = false) email: String?
    ): Page<ApplicantDTO>? {
        if (pesel != null) return irkService.getApplicantsByPesel(pesel)
        if (surname != null) return irkService.getApplicantsBySurname(surname)
        if (email != null) return irkService.getApplicantsByEmail(email)
        throw IllegalArgumentException()
    }

    @GetMapping("/registrations/{id}")
    fun getRegistration(@PathVariable("id") id: String): RegistrationDTO? = irkService.getRegistration(id)


    @GetMapping("/registrations/codes")
    fun getRegistrationCodes(): MutableList<Map<String, String>> = irkService.getAvailableRegistrations()

    @GetMapping("/registrations/codes/{id}")
    fun getRegistrationCodes(@PathVariable("id") id: String): List<String?> = irkService.getAvailableRegistrationProgrammes(id)


    @GetMapping("/applications/{id}")
    fun getApplication(@PathVariable("id") id: Long): ApplicationDTO? = irkService.getApplication(id)

    @GetMapping("/applications")
    fun getApplications(
            @RequestParam(required = false) admitted: Boolean,
            @RequestParam(required = false) paid: Boolean,
            @RequestParam(required = false) programme: String?,
            @RequestParam(required = false) registration: String?,
            @RequestParam(required = false) pageNumber: Int?
    ): Page<ApplicationDTO>? = irkService.getApplications(admitted, paid, programme, registration, pageNumber)

    @GetMapping("/programmesGroups/{id}")
    fun getProgrammesGroups(@PathVariable("id") id: String): ProgrammeGroupsDTO? = irkService.getProgrammesGroups(id)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/import")
    fun createImport(
            @RequestBody importDto: ImportDto
    ): Import = importService.create(
            programmeCode = importDto.programmeCode,
            registration = importDto.registration,
            indexPoolCode = importDto.indexPoolCode,
            startDate = importDto.startDate,
            dateOfAddmision = importDto.dateOfAddmision,
            stageCode = importDto.stageCode,
            didacticCycleCode = importDto.didacticCycleCode
    )

    @PutMapping("/import/{id}")
    fun importApplicants(@PathVariable("id") importId: Long): ResponseEntity<Void> {
        importService.getForApplicantImport(importId)
        importService.setImportStatus(ImportStatus.STARTED, importId)
        importService.resetImportedApplications(importId)
        asyncService.importApplicantsAsync(importId)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/import/{id}")
    fun getImport(@PathVariable("id") importId: Long): Import = importService.get(importId)


    @GetMapping("/import/{id}/progress")
    fun getProgress(@PathVariable("id") importId: Long): ImportProgress = importService.getProgress(importId)


    @GetMapping("/import/{id}/save")
    fun savePersons(@PathVariable("id") importId: Long): ResponseEntity<Void> {
        importService.getForPersonSave(importId)
        importService.setImportStatus(ImportStatus.SAVING, importId)
        importService.resetSaveErrors(importId)
        asyncService.savePersons(importId)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/import")
    fun getImportsPage(pageable: Pageable): org.springframework.data.domain.Page<Import> = importService.getAll(pageable)


    @DeleteMapping("/import/{id}")
    fun deleteImport(@PathVariable("id") importId: Long) = importService.delete(importId)


    @GetMapping("/import/{id}/applications")
    fun findAllApplicationsByImportId(
            pageable: Pageable,
            @PathVariable("id") importId: Long)
            : org.springframework.data.domain.Page<Application> = applicationService.findAllApplicationsByImportId(pageable, importId)


    @GetMapping("/indexPool")
    fun getAvailableIndexPools(): List<IndexTypeDto> = usosService.getAvailableIndexPoolsCodes()


    @GetMapping("/programme/{code}/stages")
    fun getAvailableIndexPools(@PathVariable("code") code: String): List<String> = usosService.getStageByProgrammeCode(code)


    @GetMapping("/didacticCycle")
    fun findDidacticCycleByCode(@RequestParam("code") didacticCycleCode: String): List<String> = usosService.findDidacticCycleCodes(didacticCycleCode, 10)


    @PutMapping("/person/{id}/indexNumber")
    fun updateIndexNumberByUsosIdAndIndexType(
            @PathVariable("id") personId: Long,
            @RequestParam("indexType") indexTypeCode: String,
            @RequestParam("indexNumber") indexNumber: String
    ) = usosService.updateIndexNumberByUsosIdAndIndexType(personId, indexTypeCode, indexNumber)

    @PutMapping("/import/{id}/archive")
    fun archiveImport(@PathVariable("id") importId: Long) {
        val import = importService.get(importId)
        if (import.importProgress?.importStatus == ImportStatus.COMPLETE)
            return importService.setImportStatus(ImportStatus.ARCHIVED, importId)
        else
            throw IllegalStateException("Zły stan importu!")
    }
}
