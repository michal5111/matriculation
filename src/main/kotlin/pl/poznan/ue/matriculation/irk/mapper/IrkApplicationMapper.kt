package pl.poznan.ue.matriculation.irk.mapper

import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.local.domain.applications.Application

class IrkApplicationMapper {

    fun mapApplicationDtoToApplication(applicationDTO: IrkApplicationDTO): Application {
        return Application(
            admitted = applicationDTO.admitted,
            comment = applicationDTO.comment,

            baseOfStay = applicationDTO.foreignerData?.baseOfStay,
            basisOfAdmission = applicationDTO.foreignerData?.basisOfAdmission,
            sourceOfFinancing = applicationDTO.foreignerData?.sourceOfFinancing,
            foreignId = applicationDTO.id,
            payment = applicationDTO.payment,
            position = applicationDTO.position,
            qualified = applicationDTO.qualified,
            score = applicationDTO.score
        )
    }

    fun update(application: Application, applicationDto: IrkApplicationDTO): Application = application.apply {
        admitted = applicationDto.admitted
        comment = applicationDto.comment
        baseOfStay = applicationDto.foreignerData?.baseOfStay
        basisOfAdmission = applicationDto.foreignerData?.basisOfAdmission
        sourceOfFinancing = applicationDto.foreignerData?.sourceOfFinancing
        payment = applicationDto.payment
        position = applicationDto.position
        qualified = applicationDto.qualified
        score = applicationDto.score
    }
}
