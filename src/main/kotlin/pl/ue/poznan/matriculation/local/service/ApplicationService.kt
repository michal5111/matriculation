package pl.ue.poznan.matriculation.local.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
import pl.ue.poznan.matriculation.irk.mapper.ApplicantMapper
import pl.ue.poznan.matriculation.irk.mapper.ApplicationMapper
import pl.ue.poznan.matriculation.irk.service.IrkService
import pl.ue.poznan.matriculation.local.domain.applications.Application
import pl.ue.poznan.matriculation.local.domain.import.Import
import pl.ue.poznan.matriculation.local.repo.ApplicantRepository
import pl.ue.poznan.matriculation.local.repo.ApplicationRepository
import pl.ue.poznan.matriculation.local.repo.ImportProgressRepository

@Service
class ApplicationService(
        private val applicationMapper: ApplicationMapper,
        private val applicationRepository: ApplicationRepository,
        private val applicantRepository: ApplicantRepository,
        private val irkService: IrkService,
        private val applicantService: ApplicantService,
        private val applicantMapper: ApplicantMapper,
        private val importProgressRepository: ImportProgressRepository
) {

    val logger: org.slf4j.Logger? = LoggerFactory.getLogger(ApplicationService::class.java)

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processApplication(import: Import, applicationDTO: ApplicationDTO) {
        logger?.debug("Sprawdzam czy zgłoszenie o irkId ${applicationDTO.id} już istnieje w bazie:")
        val application = if (applicationRepository.existsByIrkId(applicationDTO.id)) {
            logger?.debug("Zgłosznie istnieje. Aktualizuję...")
            updateApplication(
                    applicationRepository.getByIrkId(applicationDTO.id),
                    applicationDTO
            )
        } else {
            logger?.debug("Zgłoszenie nie istnieje. Tworzę nowe...")
            applicationMapper.applicationDtoToApplicationMapper(applicationDTO)
        }
        logger?.debug("Sprawdzam czy kandydat o irkId ${applicationDTO.user} istnieje w bazie:")
        val applicant = irkService.getApplicantById(applicationDTO.user).let {
            if (applicantRepository.existsByIrkId(it!!.id)) {
                logger?.debug("Kandydat istnieje. Aktualizuję...")
                val applicant = applicantRepository.findByIrkId(it.id)
                return@let applicantService.updateApplicant(applicant!!, it)
            } else {
                logger?.debug("Kandydat nie istnieje. Tworzę nowego...")
                return@let applicantMapper.applicantDtoToApplicantMapper(it)
            }
        }
        logger?.debug("Zapisuję kandydata...")
        applicantRepository.save(applicant)
        if (!applicant.applications.any { it.irkId == application.irkId }) {
            application.applicant = applicant
            applicant.applications.add(application)
        }
        logger?.debug("Zapisuję zgłoszenie")
        applicationRepository.save(application)
        if (!import.applications.any { it.irkId == application.irkId }) {
            import.applications.add(application)
            application.import = import
        }
        import.importProgress!!.importedApplications++
        logger?.debug("Zapisuję progres...")
        importProgressRepository.save(import.importProgress!!)
    }

    fun findAllApplicationsByImportId(pageable: Pageable, importId: Long): Page<Application> {
        return applicationRepository.findAllByImportId(pageable, importId)
    }

    fun updateApplication(application: Application, applicationDTO: ApplicationDTO): Application {
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