package pl.poznan.ue.matriculation.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.poznan.ue.matriculation.local.dto.UrlDto
import pl.poznan.ue.matriculation.oracle.dto.IndexTypeDto
import pl.poznan.ue.matriculation.oracle.service.UsosService

@RestController
@RequestMapping("/api/usos")
class UsosController(private val usosService: UsosService) {

    @Value("\${pl.poznan.ue.matriculation.usos.url}")
    private lateinit var usosUrl: String

    @GetMapping("/indexPool")
    fun getAvailableIndexPools(): List<IndexTypeDto> = usosService.getAvailableIndexPoolsCodes()


    @GetMapping("/programme/{code}/stages")
    fun getAvailableStages(@PathVariable("code") code: String): List<String> = usosService.getStageByProgrammeCode(code)


    @GetMapping("/didacticCycle")
    fun findDidacticCycleByCode(@RequestParam("code") didacticCycleCode: String): List<String> =
        usosService.findDidacticCycleCodes(didacticCycleCode, 10)


    @PutMapping("/person/{id}/indexNumber")
    fun updateIndexNumberByUsosIdAndIndexType(
        @PathVariable("id") personId: Long,
        @RequestParam("indexType") indexTypeCode: String,
        @RequestParam("indexNumber") indexNumber: String
    ) = usosService.updateIndexNumberByUsosIdAndIndexType(personId, indexTypeCode, indexNumber)

    @GetMapping("/url")
    fun getUsosUrl(): UrlDto = UrlDto(usosUrl)
}
