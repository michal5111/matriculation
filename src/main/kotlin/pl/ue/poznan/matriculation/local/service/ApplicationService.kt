package pl.ue.poznan.matriculation.local.service

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

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processApplicant(import: Import, applicationDTO: ApplicationDTO) {
        val application = applicationMapper.applicationDtoToApplicationMapper(applicationDTO)
        val applicant = irkService.getApplicantById(applicationDTO.user).let {
            if (applicantRepository.existsByIrkId(it!!.id)) {
                val applicant = applicantRepository.findByIrkId(it.id)
                return@let applicantService.updateApplicant(applicant!!, it)
            } else {
                return@let applicantMapper.applicantDtoToApplicantMapper(it)
            }
        }
        applicantRepository.save(applicant)
        application.applicant = applicant
        applicant.application.add(application)
        applicationRepository.save(application)
        import.addApplication(application)
        application.import = import
        import.importProgress!!.importedApplications++
        importProgressRepository.save(import.importProgress!!)
        //importRepository.save(import)
    }
    
    fun findAllApplicationsByImportId(pageable: Pageable, importId: Long): Page<Application> {
        return applicationRepository.findAllByImportId(pageable, importId)
    }
}