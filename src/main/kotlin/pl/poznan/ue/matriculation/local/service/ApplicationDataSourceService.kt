package pl.poznan.ue.matriculation.local.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.exception.DataSourceNotFoundException
import pl.poznan.ue.matriculation.local.dto.DataSourceDto
import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import pl.poznan.ue.matriculation.local.dto.IApplicationDto

@Service
class ApplicationDataSourceService(applicationDataSources: List<IApplicationDataSource<*, *>>) {

    private val dataSourcesMap = HashMap<String, IApplicationDataSource<IApplicationDto, IApplicantDto>>()

    fun getDataSources(): List<DataSourceDto> {
        return dataSourcesMap.map {
            DataSourceDto(
                name = it.value.name,
                id = it.key
            )
        }
    }

    fun getDataSource(id: String): IApplicationDataSource<IApplicationDto, IApplicantDto> {
        return dataSourcesMap[id] ?: throw DataSourceNotFoundException()
    }

    init {
        applicationDataSources as List<IApplicationDataSource<IApplicationDto, IApplicantDto>>
        applicationDataSources.forEach {
            dataSourcesMap[it.id] = it
        }
    }
}