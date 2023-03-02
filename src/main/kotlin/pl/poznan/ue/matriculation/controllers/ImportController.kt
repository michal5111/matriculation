package pl.poznan.ue.matriculation.controllers

import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.poznan.ue.matriculation.kotlinExtensions.toDto
import pl.poznan.ue.matriculation.kotlinExtensions.toPageDto
import pl.poznan.ue.matriculation.local.dto.*
import pl.poznan.ue.matriculation.local.job.JobType
import pl.poznan.ue.matriculation.local.service.ApplicantService
import pl.poznan.ue.matriculation.local.service.ApplicationService
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.JobService
import javax.validation.Valid

@RestController
@RequestMapping("/api/import")
class ImportController(
    private val importService: ImportService,
    private val jobService: JobService,
    private val applicantService: ApplicantService,
    private val applicationService: ApplicationService
) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createImport(
        @RequestBody @Valid importDto: ImportDto
    ): ImportDto = importService.create(importDto).toDto()

    @PutMapping
    fun updateImport(
        @RequestBody @Valid importDto: ImportDto
    ): ImportDto = importService.updateImport(importDto).toDto()

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun importApplicants(@PathVariable("id") importId: Long) = jobService.runJob(JobType.IMPORT, importId)

    @GetMapping("/{id}")
    fun findImportById(@PathVariable("id") importId: Long): ImportDto = importService.findById(importId).toDto()

    @GetMapping("/{id}/save")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun savePersons(@PathVariable("id") importId: Long) = jobService.runJob(JobType.SAVE, importId)

    @GetMapping
    fun getImportsPage(pageable: Pageable): PageDto<ImportDto> = importService.getAll(pageable).toPageDto {
        it.toDto()
    }

    @DeleteMapping("/{id}")
    fun deleteImport(@PathVariable("id") importId: Long) {
        importService.delete(importId)
        applicantService.deleteOrphaned()
    }

    @GetMapping("/{id}/applications")
    fun findAllApplicationsByImportId(
        pageable: Pageable,
        @PathVariable("id") importId: Long
    ): PageDto<ApplicationDto> = applicationService.findAllApplicationsByImportId(pageable, importId).toPageDto {
        it.toDto()
    }
}
