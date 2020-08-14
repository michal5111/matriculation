package pl.poznan.ue.matriculation.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.domain.import.ImportProgress
import pl.poznan.ue.matriculation.local.dto.*
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceService
import pl.poznan.ue.matriculation.local.service.ApplicationService
import pl.poznan.ue.matriculation.local.service.AsyncService
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.oracle.dto.IndexTypeDto
import pl.poznan.ue.matriculation.oracle.service.UsosService

@RestController
@RequestMapping("/api")
class RestController(
        private val applicationDataSourceService: ApplicationDataSourceService,
        private val importService: ImportService,
        private val asyncService: AsyncService,
        private val usosService: UsosService,
        private val applicationService: ApplicationService
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

//    @GetMapping("/applicants/{dataSourceType}/{id}")
//    fun getApplicantById(@PathVariable("id") id: Long, @PathVariable("dataSourceType") dataSourceType: String): Applicant = applicationDataSourceService
//            .getDataSource(dataSourceType)
//            .getApplicantById(id) as Applicant

//    @GetMapping("/applicants/")
//    fun getApplicantByParam(
//            @RequestParam(required = false) pesel: String?,
//            @RequestParam(required = false) surname: String?,
//            @RequestParam(required = false) email: String?
//    ): Page<ApplicantDTO>? {
//        if (pesel != null) return irkService.getApplicantsByPesel(pesel)
//        if (surname != null) return irkService.getApplicantsBySurname(surname)
//        if (email != null) return irkService.getApplicantsByEmail(email)
//        throw IllegalArgumentException()
//    }

//    @GetMapping("/registrations/{dataSourceType}/{id}")
//    fun getRegistration(@PathVariable("id") id: String, @PathVariable("dataSourceType") dataSourceType: String): Registration? = applicationDataSourceService
//            .getDataSource(dataSourceType)
//            .getRegistrationByCode(id)


    @GetMapping("/registrations/{dataSourceType}/codes")
    fun getRegistrationCodes(
            @PathVariable("dataSourceType") dataSourceType: String
    ): List<RegistrationDto> = applicationDataSourceService
            .getDataSource(dataSourceType)
            .getAvailableRegistrations()

    @GetMapping("/registrations/{dataSourceType}/codes/{id}")
    fun getProgrammesCodes(
            @PathVariable("id") id: String,
            @PathVariable("dataSourceType") dataSourceType: String): List<ProgrammeDto> = applicationDataSourceService
            .getDataSource(dataSourceType)
            .getAvailableRegistrationProgrammes(id)


//    @GetMapping("/applications/{dataSourceType}/{id}")
//    fun getApplication(@PathVariable("id") id: Long, @PathVariable("dataSourceType") dataSourceType: String): Application? = applicationDataSourceService
//            .getDataSource(dataSourceType)
//            .getApplicationById(id)

//    @GetMapping("/applications/{dataSourceType}")
//    fun getApplications(
//            programme: String,
//            registration: String,
//            pageNumber: Int,
//            @PathVariable("dataSourceType") dataSourceType: String
//    ): Page<Application> = applicationDataSourceService
//            .getDataSource(dataSourceType)
//            .getApplicationsPage(registration, programme, pageNumber)

//    @GetMapping("/programmesGroups/{id}")
//    fun getProgrammesGroups(@PathVariable("id") id: String): ProgrammeGroupsDTO? = irkService.getProgrammesGroups(id)

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
            dataSourceType = importDto.dataSourceId
    )

    @PutMapping("/import/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun importApplicants(@PathVariable("id") importId: Long) {
        importService.prepareForImporting(importId)
        asyncService.importApplicantsAsync(importId)
    }

    @GetMapping("/import/{id}")
    fun getImport(@PathVariable("id") importId: Long): Import = importService.get(importId)


    @GetMapping("/import/{id}/progress")
    fun getProgress(@PathVariable("id") importId: Long): ImportProgress = importService.getProgress(importId)

    @GetMapping("/import/{id}/save")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun savePersons(@PathVariable("id") importId: Long) {
        importService.prepareForSaving(importId)
        asyncService.savePersons(importId)
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


    @GetMapping("/usos/indexPool")
    fun getAvailableIndexPools(): List<IndexTypeDto> = usosService.getAvailableIndexPoolsCodes()


    @GetMapping("/usos/programme/{code}/stages")
    fun getAvailableIndexPools(@PathVariable("code") code: String): List<String> = usosService.getStageByProgrammeCode(code)


    @GetMapping("/usos/didacticCycle")
    fun findDidacticCycleByCode(@RequestParam("code") didacticCycleCode: String): List<String> = usosService.findDidacticCycleCodes(didacticCycleCode, 10)


    @PutMapping("/usos/person/{id}/indexNumber")
    fun updateIndexNumberByUsosIdAndIndexType(
            @PathVariable("id") personId: Long,
            @RequestParam("indexType") indexTypeCode: String,
            @RequestParam("indexNumber") indexNumber: String
    ) = usosService.updateIndexNumberByUsosIdAndIndexType(personId, indexTypeCode, indexNumber)

    @PutMapping("/import/{id}/archive")
    fun archiveImport(@PathVariable("id") importId: Long) {
        val import = importService.get(importId)
        if (import.importProgress.importStatus == ImportStatus.COMPLETE) {
            asyncService.archiveApplicants(importId)
            return importService.setImportStatus(ImportStatus.ARCHIVED, importId)
        } else
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Zły stan importu!")
    }

    @GetMapping("/import/dataSources")
    fun getDataSources(): List<DataSourceDto> {
        return applicationDataSourceService.getDataSources()
    }

    @GetMapping("/usos/url")
    fun getUsosUrl(): UrlDto {
        return UrlDto(usosUrl)
    }
}
