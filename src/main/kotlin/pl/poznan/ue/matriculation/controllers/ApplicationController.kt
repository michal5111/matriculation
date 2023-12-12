package pl.poznan.ue.matriculation.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.poznan.ue.matriculation.kotlinExtensions.toDto
import pl.poznan.ue.matriculation.local.dto.ApplicantUsosIdAndPotentialDuplicateStatusDto
import pl.poznan.ue.matriculation.local.dto.ApplicationDto
import pl.poznan.ue.matriculation.local.dto.PageDto
import pl.poznan.ue.matriculation.local.service.ApplicationService

@RestController
@RequestMapping("/api/applications")
class ApplicationController(private val applicationService: ApplicationService) {
    @DeleteMapping("/{id}")
    fun deleteApplication(
        @PathVariable("id") applicationId: Long
    ) {
        return applicationService.delete(applicationId)
    }

    @PutMapping("/{id}/potentialDuplicateStatus")
    fun updatePotentialDuplicateStatus(
        @PathVariable("id") applicationId: Long,
        @RequestBody potentialDuplicateStatusDto: ApplicantUsosIdAndPotentialDuplicateStatusDto
    ): ApplicationDto {
        return applicationService.updatePotentialDuplicateStatus(applicationId, potentialDuplicateStatusDto)
    }

    @GetMapping
    fun findAll(
        @RequestParam(required = false) importId: Long?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) surname: String?,
        @RequestParam(required = false) pesel: String?,
        pageable: Pageable,
    ): PageDto<ApplicationDto> {
        return applicationService.findAll(importId, name, surname, pesel, pageable)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ApplicationDto? {
        return applicationService.findById(id)?.toDto()
    }
}
