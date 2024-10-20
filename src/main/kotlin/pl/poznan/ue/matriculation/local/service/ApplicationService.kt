package pl.poznan.ue.matriculation.local.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.exception.ApplicationNotFoundException
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.kotlinExtensions.toDto
import pl.poznan.ue.matriculation.kotlinExtensions.toPageDto
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.dto.ApplicantUsosIdAndPotentialDuplicateStatusDto
import pl.poznan.ue.matriculation.local.dto.ApplicationDto
import pl.poznan.ue.matriculation.local.dto.PageDto
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.specification.ApplicationsByImportId
import pl.poznan.ue.matriculation.local.specification.ApplicationsByName
import pl.poznan.ue.matriculation.local.specification.ApplicationsByPesel
import pl.poznan.ue.matriculation.local.specification.ApplicationsBySurname
import java.util.stream.Stream

@Service
@Transactional(rollbackFor = [Exception::class, RuntimeException::class])
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val applicantService: ApplicantService
) {
    private val logger = LoggerFactory.getLogger(ApplicationService::class.java)

    fun findAllApplicationsByImportId(pageable: Pageable, importId: Long): Page<Application> {
        return applicationRepository.findAllByImportId(pageable, importId)
    }

    fun updatePotentialDuplicateStatus(
        applicationId: Long,
        potentialDuplicateStatusDto: ApplicantUsosIdAndPotentialDuplicateStatusDto,
        userDetails: UserDetails
    ): ApplicationDto {
        val application = applicationRepository.findByIdOrNull(applicationId) ?: throw ApplicationNotFoundException()
        val applicantId = application.applicant?.id ?: throw ApplicantNotFoundException()
        val applicant = applicantService.findWithIdentityDocumentsById(applicantId)
            ?: throw ApplicantNotFoundException()
        applicant.potentialDuplicateStatus = potentialDuplicateStatusDto.potentialDuplicateStatus
        applicant.potentialDuplicateStatusConfirmedBy = userDetails.username
        applicant.usosId = potentialDuplicateStatusDto.usosId
        val import = application.import ?: throw ImportNotFoundException()
        import.potentialDuplicates--
        logger.info("Potential duplicate status updated by $userDetails for application with id: $applicationId to ${potentialDuplicateStatusDto.potentialDuplicateStatus}")
        return application.toDto()
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

    fun findAllIdsByImportIdAndImportStatusIn(importId: Long, statusList: List<ApplicationImportStatus>): List<Long> {
        return applicationRepository.findAllIdsByImportIdAndImportStatusIn(importId, statusList)
    }

    fun findAll(
        importId: Long?,
        name: String?,
        surname: String?,
        pesel: String?,
        pageable: Pageable
    ): PageDto<ApplicationDto> {
        val byImportId = ApplicationsByImportId(importId)
        val byName = ApplicationsByName(name)
        val bySurname = ApplicationsBySurname(surname)
        val byPesel = ApplicationsByPesel(pesel)
        val spec: Specification<Application> = Specification
            .where(byImportId)
            .and(byName)
            .and(bySurname)
            .and(byPesel)
        return applicationRepository.findAll(spec, pageable).toPageDto {
            it.toDto()
        }
    }

    fun findAllStreamByImportId(importId: Long): Stream<Application> {
        return applicationRepository.findAllStreamByImportId(importId)
    }

    fun findById(id: Long): Application? {
        return applicationRepository.findByIdOrNull(id)
    }

    fun findWithApplicantById(id: Long): Application? {
        return applicationRepository.findWithApplicantById(id)
    }

    fun findAllByImportIdAndNotificationSent(
        importId: Long,
        sent: Boolean,
        importStatus: ApplicationImportStatus
    ): Stream<Application> {
        return applicationRepository.findAllByImportIdAndNotificationSentAndApplicantUidNotNullAndImportStatus(
            importId,
            sent,
            importStatus
        )
    }

    fun findAllStreamByImportIdAndApplicantPotentialDuplicateStatusIn(
        importId: Long,
        duplicateStatusList: List<DuplicateStatus>
    ): Stream<Application> {
        return applicationRepository.findAllStreamByImportIdAndApplicantPotentialDuplicateStatusIn(
            importId,
            duplicateStatusList
        )
    }

    fun findAllForArchive(importId: Long): Stream<Application> {
        return applicationRepository.findAllForArchive(importId)
    }

    fun delete(applicationId: Long) {
        val application = applicationRepository.findByIdOrNull(applicationId)
        val import = application?.import ?: throw ImportNotFoundException()
        applicationRepository.deleteById(applicationId)
        application.applicant?.id?.let {
            applicantService.deleteOrphanedById(it)
        }
        import.importedApplications--
        import.totalCount = (import.totalCount ?: throw IllegalStateException()) - 1
        if (application.importStatus == ApplicationImportStatus.ERROR) {
            import.saveErrors--
        }
        if (import.importStatus == ImportStatus.COMPLETED_WITH_ERRORS && import.saveErrors == 0) {
            import.importStatus = ImportStatus.COMPLETE
        }
    }
}
