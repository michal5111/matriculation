package pl.poznan.ue.matriculation.local.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.exception.ApplicationNotFoundException
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import pl.poznan.ue.matriculation.local.dto.ApplicantUsosIdAndPotentialDuplicateStatusDto
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import java.util.stream.Stream

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val applicantService: ApplicantService
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

    fun save(application: Application): Application {
        return applicationRepository.save(application)
    }

    fun findByForeignIdAndDataSourceId(foreignId: Long, dataSourceId: String): Application? {
        return applicationRepository.findByForeignIdAndDataSourceId(foreignId, dataSourceId)
    }

    fun getAllByImportIdAndImportStatusIn(
        importId: Long,
        statusList: List<ApplicationImportStatus>,
        sort: Sort
    ): Stream<Application> {
        return applicationRepository.getAllByImportIdAndImportStatusIn(importId, statusList, sort)
    }

    @Transactional
    fun findAllByImportId(importId: Long): List<Application> {
        return applicationRepository.findAllByImportId(importId)
    }

    @Transactional
    fun findAllStreamByImportId(importId: Long): Stream<Application> {
        return applicationRepository.findAllStreamByImportId(importId)
    }

    @Transactional
    fun findAllByImportIdAndNotificationSent(importId: Long, sent: Boolean): Stream<Application> {
        return applicationRepository.findAllByImportIdAndNotificationSentAndApplicantUidNotNull(importId, sent)
    }

    @Transactional
    fun findAllStreamByImportIdAndApplicantPotentialDuplicateStatusIn(
        importId: Long,
        duplicateStatusList: List<DuplicateStatus>
    ): Stream<Application> {
        return applicationRepository.findAllStreamByImportIdAndApplicantPotentialDuplicateStatusIn(
            importId,
            duplicateStatusList
        )
    }

    @Transactional
    fun findAllForArchive(importId: Long): Stream<Application> {
        return applicationRepository.findAllForArchive(importId)
    }

    @Transactional
    fun delete(applicationId: Long) {
        val application = applicationRepository.findByIdOrNull(applicationId)
        val import = application?.import ?: throw ImportNotFoundException()
        applicationRepository.deleteById(applicationId)
        application.applicant?.id?.let {
            applicantService.deleteOrphanedById(it)
        }
        import.importedApplications--
        import.totalCount = (import.totalCount ?: return) - 1
    }
}
