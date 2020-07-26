package pl.poznan.ue.matriculation.dreamApply.mapper

import pl.poznan.ue.matriculation.dreamApply.dto.application.ApplicationDto
import pl.poznan.ue.matriculation.local.domain.applications.Application

class DreamApplyApplicationMapper {

    fun map(applicationDto: ApplicationDto): Application {
        return Application(
                foreignId = applicationDto.id,
                admitted = applicationDto.status,
                payment = "paid",
                comment = null,
                position = null,
                qualified = "qualified",
                score = null,
                applicationForeignerData = null
        )
    }

    fun update(application: Application, applicationDto: ApplicationDto): Application {
        application.apply {
            admitted = applicationDto.status
            payment = "paid"
            comment = null
            position = null
            qualified = "qualified"
            score = null
            applicationForeignerData
        }
        return application
    }
}