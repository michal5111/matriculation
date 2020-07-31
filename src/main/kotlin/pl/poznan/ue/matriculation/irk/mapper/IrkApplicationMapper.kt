package pl.poznan.ue.matriculation.irk.mapper

import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.applications.ApplicationForeignerData

class IrkApplicationMapper {

    fun mapApplicationDtoToApplication(applicationDTO: IrkApplicationDTO): Application {
        return Application(
                admitted = applicationDTO.admitted,
                comment = applicationDTO.comment,
                applicationForeignerData = applicationDTO.foreignerData?.let {
                    ApplicationForeignerData(
                            baseOfStay = it.baseOfStay,
                            basisOfAdmission = it.basisOfAdmission,
                            sourceOfFinancing = it.sourceOfFinancing
                    )
                },
                foreignId = applicationDTO.id,
                payment = applicationDTO.payment,
                position = applicationDTO.position,
                qualified = applicationDTO.qualified,
                score = applicationDTO.score
//                turn = Turn(
//                        dateFrom = applicationDTO.turn.dateFrom,
//                        dateTo = applicationDTO.turn.dateTo,
//                        programme = applicationDTO.turn.programme,
//                        registration = applicationDTO.turn.registration
//                ),
        ).apply {
            applicationForeignerData?.application = this
        }
    }

    fun update(application: Application, applicationDto: IrkApplicationDTO): Application {
        application.apply {
            admitted = applicationDto.admitted
            comment = applicationDto.comment
            applicationForeignerData?.apply {
                baseOfStay = applicationDto.foreignerData?.baseOfStay
                basisOfAdmission = applicationDto.foreignerData?.basisOfAdmission
                sourceOfFinancing = applicationDto.foreignerData?.sourceOfFinancing
            }
            payment = applicationDto.payment
            position = applicationDto.position
            qualified = applicationDto.qualified
            score = applicationDto.score
        }
        return application
    }
}