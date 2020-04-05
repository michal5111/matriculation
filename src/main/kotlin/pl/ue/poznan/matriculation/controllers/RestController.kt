package pl.ue.poznan.matriculation.controllers

import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.ue.poznan.matriculation.irk.dto.Page
import pl.ue.poznan.matriculation.irk.dto.applicants.ApplicantDTO
import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
import pl.ue.poznan.matriculation.irk.dto.programmes.ProgrammeGroupsDTO
import pl.ue.poznan.matriculation.irk.dto.registrations.RegistrationDTO
import pl.ue.poznan.matriculation.irk.service.IrkService
import pl.ue.poznan.matriculation.local.domain.applications.Application
import pl.ue.poznan.matriculation.local.domain.enum.ImportStatus
import pl.ue.poznan.matriculation.local.domain.import.Import
import pl.ue.poznan.matriculation.local.domain.import.ImportDto
import pl.ue.poznan.matriculation.local.domain.import.ImportProgress
import pl.ue.poznan.matriculation.local.service.ApplicationService
import pl.ue.poznan.matriculation.local.service.AsyncService
import pl.ue.poznan.matriculation.local.service.ImportService
import pl.ue.poznan.matriculation.oracle.service.UsosService

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
    fun getApplicantById(@PathVariable("id") id: Long): ApplicantDTO? {
        return irkService.getApplicantById(id)
    }

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
    fun getRegistration(@PathVariable("id") id: String): RegistrationDTO? {
        return irkService.getRegistration(id)
    }

    @GetMapping("/registrations/codes")
    fun getRegistrationCodes(): List<String> {
        return irkService.getAvailableRegistrations()
    }

    @GetMapping("/registrations/codes/{id}")
    fun getRegistrationCodes(@PathVariable("id") id: String): List<String?> {
        return irkService.getAvailableRegistrationProgrammes(id)
    }

    @GetMapping("/applications/{id}")
    fun getApplication(@PathVariable("id") id: Long): ApplicationDTO? {
        return irkService.getApplication(id)
    }

    @GetMapping("/applications")
    fun getApplications(
            @RequestParam(required = false) admitted: Boolean,
            @RequestParam(required = false) paid: Boolean,
            @RequestParam(required = false) programme: String?,
            @RequestParam(required = false) registration: String?,
            @RequestParam(required = false) pageNumber: Int?
    ): Page<ApplicationDTO>? {
        return irkService.getApplications(admitted, paid, programme, registration, pageNumber)
    }

    @GetMapping("/programmesGroups/{id}")
    fun getProgrammesGroups(@PathVariable("id") id: String): ProgrammeGroupsDTO? {
        return irkService.getProgrammesGroups(id)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/import")
    fun createImport(
            @RequestBody importDto: ImportDto
    ): Import {
        return importService.createImport(
                programmeCode = importDto.programmeCode,
                registration = importDto.registration,
                indexPoolCode = importDto.indexPoolCode,
                startDate = importDto.startDate,
                dateOfAddmision = importDto.dateOfAddmision,
                stageCode = importDto.stageCode,
                didacticCycleCode = importDto.didacticCycleCode
        )
    }

    @PutMapping("/import/{id}")
    fun importApplicants(@PathVariable("id") importId: Long): ResponseEntity<Void> {
        val import = importService.getImportForApplicantImport(importId)
        importService.setImportStatus(ImportStatus.STARTED, importId)
        asyncService.importApplicantsAsync(import)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/import/{id}")
    fun getImport(@PathVariable("id") importId: Long): Import {
        return importService.getImport(importId)
    }

    @GetMapping("/import/progress/{id}")
    fun getProgress(@PathVariable("id") importId: Long): ImportProgress {
        return importService.getProgress(importId)
    }

    @GetMapping("/import/save/{id}")
    fun savePersons(@PathVariable("id") importId: Long): ResponseEntity<Void> {
        val import = importService.getImportForPersonSave(importId)
        importService.setImportStatus(ImportStatus.SAVING, importId)
        importService.resetSaveErrors(importId)
        asyncService.savePersons(import)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/import")
    fun getImportsPage(pageable: Pageable): org.springframework.data.domain.Page<Import> {
        return importService.getAllImports(pageable)
    }

    @DeleteMapping("/import/{id}")
    fun deleteImport(@PathVariable("id") importId: Long) {
        return importService.deleteImport(importId)
    }

    @GetMapping("/import/{id}/applications")
    fun findAllApplicationsByImportId(pageable: Pageable, @PathVariable("id") importId: Long): org.springframework.data.domain.Page<Application> {
        return applicationService.findAllApplicationsByImportId(pageable, importId)
    }

    @GetMapping("/indexPool")
    fun getAvailableIndexPools(): List<String> {
        return usosService.getAvailableIndexPoolsCodes()
    }

    @GetMapping("/programme/{code}/stages")
    fun getAvailableIndexPools(@PathVariable("code") code: String): List<String> {
        return usosService.getStageByProgrammeCode(code)
    }

    @GetMapping("/didacticCycle/{code}")
    fun findDidacticCycleByCode(@PathVariable("code")didacticCycleCode: String): List<String> {
        return usosService.findDidacticCycleCodes(didacticCycleCode,10)
    }
}
