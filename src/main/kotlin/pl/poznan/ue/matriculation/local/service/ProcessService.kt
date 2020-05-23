package pl.poznan.ue.matriculation.local.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.irk.dto.applications.ApplicationDTO
import pl.poznan.ue.matriculation.irk.mapper.ApplicantMapper
import pl.poznan.ue.matriculation.irk.mapper.ApplicationMapper
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.service.PersonService

@Service
class ProcessService {

    @Autowired
    lateinit var importRepository: ImportRepository

    @Autowired
    lateinit var applicationRepository: ApplicationRepository

    @Autowired
    lateinit var applicantRepository: ApplicantRepository

    @Autowired
    lateinit var applicationMapper: ApplicationMapper

    @Autowired
    lateinit var irkService: IrkService

    @Autowired
    lateinit var applicantMapper: ApplicantMapper

    @Autowired
    lateinit var applicantService: ApplicantService

    @Autowired
    lateinit var personService: PersonService

    @Value("\${pl.poznan.ue.matriculation.setAsAccepted}")
    private var setAsAccepted: Boolean = false

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processApplication(importId: Long, applicationDTO: ApplicationDTO) {
        val import = importRepository.getOne(importId)
        val application = if (applicationRepository.existsByIrkId(applicationDTO.id)) {
            updateApplication(
                    applicationRepository.getByIrkId(applicationDTO.id),
                    applicationDTO
            )
        } else {
            applicationMapper.applicationDtoToApplicationMapper(applicationDTO)
        }
        val applicant = irkService.getApplicantById(applicationDTO.user).let {
            if (applicantRepository.existsByIrkId(it!!.id)) {
                val applicant = applicantRepository.findByIrkId(it.id)
                return@let applicantService.updateApplicant(applicant!!, it)
            } else {
                return@let applicantMapper.applicantDtoToApplicantMapper(it)
            }
        }
        applicantRepository.save(applicant)
        if (!applicant.applications.any { it.irkId == application.irkId }) {
            application.applicant = applicant
            applicant.applications.add(application)
        }
        applicationRepository.save(application)
        if (!applicationRepository.existsByImportIdAndIrkId(importId, application.irkId)) {
            import.applications.add(application)
            application.import = import
        }
        import.importProgress!!.importedApplications++
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

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processPerson(application: Application, importId: Long) {
        val import = importRepository.getOne(importId)
        val personIdAndAssignedNumber = personService.processPerson(import, application)
        personIdAndAssignedNumber.let { pair ->
            application.applicant!!.usosId = pair.first
            application.applicant!!.assignedIndexNumber = pair.second
        }
        application.importError = null
        application.stackTrace = null
        application.applicationImportStatus = ApplicationImportStatus.IMPORTED
        if (setAsAccepted) {
            irkService.completeImmatriculation(application.irkId)
        }
    }
}