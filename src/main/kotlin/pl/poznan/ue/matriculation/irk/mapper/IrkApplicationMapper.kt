package pl.poznan.ue.matriculation.irk.mapper

import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.local.domain.applications.Application

class IrkApplicationMapper {

    fun mapApplicationDtoToApplication(applicationDTO: IrkApplicationDTO): Application {
        return Application(
            baseOfStay = applicationDTO.foreignerData?.baseOfStay,
            basisOfAdmission = applicationDTO.foreignerData?.basisOfAdmission,
            sourceOfFinancing = applicationDTO.foreignerData?.sourceOfFinancing,
            foreignId = applicationDTO.id
        )
    }

    fun update(application: Application, applicationDto: IrkApplicationDTO): Application = application.apply {
        baseOfStay = applicationDto.foreignerData?.baseOfStay
        basisOfAdmission = applicationDto.foreignerData?.basisOfAdmission
        sourceOfFinancing = applicationDto.foreignerData?.sourceOfFinancing
    }
}
