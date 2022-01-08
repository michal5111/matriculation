package pl.poznan.ue.matriculation.local.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.exception.ApplicationNotFoundException
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.dto.ApplicantUsosIdAndPotentialDuplicateStatusDto
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository
) {
    fun findAllApplicationsByImportId(pageable: Pageable, importId: Long): Page<Application> {
        return applicationRepository.findAllByImportId(pageable, importId)
    }

    @Transactional
    fun updatePotentialDuplicateStatus(
        applicationId: Long,
        potentialDuplicateStatusDto: ApplicantUsosIdAndPotentialDuplicateStatusDto
    ): Application {
        val application = applicationRepository.findByIdOrNull(applicationId) ?: throw ApplicationNotFoundException()
        val applicant = application.applicant ?: throw ApplicantNotFoundException()
        applicant.potentialDuplicateStatus = potentialDuplicateStatusDto.potentialDuplicateStatus
        applicant.usosId = potentialDuplicateStatusDto.usosId
        val import = application.import ?: throw ImportNotFoundException()
        import.potentialDuplicates--
        return application
    }
}
