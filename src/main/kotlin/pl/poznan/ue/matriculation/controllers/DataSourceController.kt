package pl.poznan.ue.matriculation.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.poznan.ue.matriculation.local.dto.DataSourceDto
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory

@RestController
@RequestMapping("/api/dataSources")
class DataSourceController(
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
) {

    @GetMapping
    fun getDataSources(): List<DataSourceDto> = applicationDataSourceFactory.getDataSources()

    @GetMapping("/{dataSourceType}/registrations/codes")
    fun getRegistrationCodes(
        @PathVariable("dataSourceType") dataSourceType: String
    ): List<RegistrationDto> = applicationDataSourceFactory
        .getDataSource(dataSourceType)
        .getAvailableRegistrations()

    @GetMapping("/{dataSourceType}/registrations/codes/{id}")
    fun getProgrammesCodes(
        @PathVariable("id") id: String,
        @PathVariable("dataSourceType") dataSourceType: String
    ): List<ProgrammeDto> = applicationDataSourceFactory
        .getDataSource(dataSourceType)
        .getAvailableRegistrationProgrammes(id)
}
