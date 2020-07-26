package pl.poznan.ue.matriculation.local.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.exception.DataSourceNotFoundException
import pl.poznan.ue.matriculation.local.dto.AbstractApplicantDto
import pl.poznan.ue.matriculation.local.dto.AbstractApplicationDto
import pl.poznan.ue.matriculation.local.dto.DataSourceDto

@Service
class ApplicationDataSourceService(applicationDataSources: List<IApplicationDataSource<*, *>>) {

    private val dataSourcesMap = HashMap<String, IApplicationDataSource<AbstractApplicationDto, AbstractApplicantDto>>()

    fun getDataSources(): List<DataSourceDto> {
        return dataSourcesMap.map {
            DataSourceDto(
                    name = it.value.getName(),
                    id = it.key
            )
        }
    }

    fun getDataSource(id: String): IApplicationDataSource<AbstractApplicationDto, AbstractApplicantDto> {
        return dataSourcesMap[id] ?: throw DataSourceNotFoundException()
    }

    init {
        applicationDataSources as List<IApplicationDataSource<AbstractApplicationDto, AbstractApplicantDto>>
        applicationDataSources.forEach {
            dataSourcesMap[it.getId()] = it
        }
    }
}