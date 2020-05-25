package pl.poznan.ue.matriculation.local.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.irk.dto.applications.ApplicationDTO
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository

@Service
class ApplicationService {

    @Autowired
    lateinit var applicationRepository: ApplicationRepository

    fun findAllApplicationsByImportId(pageable: Pageable, importId: Long): Page<Application> {
        return applicationRepository.findAllByImportId(pageable, importId)
    }

    fun update(application: Application, applicationDTO: ApplicationDTO): Application {
        application.apply {
            admitted = applicationDTO.admitted
            comment = applicationDTO.comment
            applicationForeignerData?.apply {
                baseOfStay = applicationDTO.foreignerData?.baseOfStay
                basisOfAdmission = applicationDTO.foreignerData?.basisOfAdmission
                sourceOfFinancing = applicationDTO.foreignerData?.sourceOfFinancing
            }
            payment = applicationDTO.payment
            position = applicationDTO.position
            qualified = applicationDTO.qualified
            score = applicationDTO.score
        }
        return application
    }
}