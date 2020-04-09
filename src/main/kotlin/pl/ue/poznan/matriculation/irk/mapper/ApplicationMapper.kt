package pl.ue.poznan.matriculation.irk.mapper

import org.springframework.stereotype.Component
import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
import pl.ue.poznan.matriculation.local.domain.Turn
import pl.ue.poznan.matriculation.local.domain.applications.Application
import pl.ue.poznan.matriculation.local.domain.applications.ApplicationForeignerData

@Component
class ApplicationMapper {

    fun applicationDtoToApplicationMapper(applicationDTO: ApplicationDTO): Application {
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
                irkId = applicationDTO.id,
                payment = applicationDTO.payment,
                position = applicationDTO.payment,
                qualified = applicationDTO.qualified,
                score = applicationDTO.score,
                turn = Turn(
                        dateFrom = applicationDTO.turn.dateFrom,
                        dateTo = applicationDTO.turn.dateTo,
                        programme = applicationDTO.turn.programme,
                        registration = applicationDTO.turn.registration
                ),
                irkInstance = applicationDTO.irkInstance!!
        ).apply {
            applicationForeignerData?.application = this
        }
    }
}