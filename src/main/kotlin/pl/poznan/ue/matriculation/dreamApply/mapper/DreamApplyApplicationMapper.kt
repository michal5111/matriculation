package pl.poznan.ue.matriculation.dreamApply.mapper

import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.local.domain.applications.Application

class DreamApplyApplicationMapper {

    fun map(dreamApplyApplicationDto: DreamApplyApplicationDto): Application {
        return Application(
            foreignId = dreamApplyApplicationDto.id,
            admitted = dreamApplyApplicationDto.status,
            payment = "paid",
            comment = null,
            position = null,
            qualified = "qualified",
            score = null,
            applicationForeignerData = null
        )
    }

    fun update(application: Application, dreamApplyApplicationDto: DreamApplyApplicationDto): Application {
        application.apply {
            admitted = dreamApplyApplicationDto.status
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