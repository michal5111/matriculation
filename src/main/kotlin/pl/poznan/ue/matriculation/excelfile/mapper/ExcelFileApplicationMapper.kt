package pl.poznan.ue.matriculation.excelfile.mapper

import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicationDto
import pl.poznan.ue.matriculation.local.domain.applications.Application

class ExcelFileApplicationMapper {

    fun mapExcelFileApplicationDtoToApplication(excelFileApplicationDto: ExcelFileApplicationDto): Application {
        return Application(
            foreignId = excelFileApplicationDto.getForeignId()
        )
    }

    fun updateApplicationFromExcelFileApplication(
        application: Application,
        excelFileApplicationDto: ExcelFileApplicationDto
    ): Application {
        return application
    }
}