package pl.poznan.ue.matriculation.local.service

import org.hibernate.JDBCException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import pl.poznan.ue.matriculation.irk.dto.applications.ApplicationDTO
import pl.poznan.ue.matriculation.irk.mapper.ApplicantMapper
import pl.poznan.ue.matriculation.irk.mapper.ApplicationMapper
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.service.PersonService
import java.util.*
import java.util.stream.Stream
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
class ProcessService(
        private val importRepository: ImportRepository,
        private val applicationRepository: ApplicationRepository,
        private val applicantRepository: ApplicantRepository,
        private val applicationMapper: ApplicationMapper,
        private val irkService: IrkService,
        private val applicantMapper: ApplicantMapper,
        private val applicantService: ApplicantService,
        private val personService: PersonService,
        private val applicationService: ApplicationService,
        private val importProgressRepository: ImportProgressRepository
) {

    val logger: Logger = LoggerFactory.getLogger(ProcessService::class.java)

    @Value("\${pl.poznan.ue.matriculation.setAsAccepted}")
    private var setAsAccepted: Boolean = false

    @Autowired
    private lateinit var self: ProcessService

    @PersistenceContext(unitName = "oracle")
    private lateinit var oracleEntityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processApplication(importId: Long, applicationDTO: ApplicationDTO): Application {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw IllegalStateException("Nie ma aktywnej transakcji")
        }
        logger.debug("Pobieram import...")
        val import = importRepository.getOne(importId)
        logger.debug("Pobrałem import... Sprawdzam czy istnieje już aplikacja o takim irkId")
        val application = if (applicationRepository.existsByIrkId(applicationDTO.id)) {
            logger.debug("Istnieje aktualizuję...")
            applicationService.update(
                    applicationRepository.getByIrkId(applicationDTO.id),
                    applicationDTO
            )
        } else {
            logger.debug("Nie istnieje dodaję...")
            applicationMapper.applicationDtoToApplicationMapper(applicationDTO)
        }
        logger.debug("Pobrałem import... Sprawdzam czy istnieje już aplikant o takim irkId")
        val applicant = irkService.getApplicantById(applicationDTO.user).let {
            if (applicantRepository.existsByIrkId(it!!.id)) {
                logger.debug("Istnieje aktualizuję...")
                val applicant = applicantRepository.findByIrkId(it.id)
                return@let applicantService.update(applicant!!, it)
            } else {
                logger.debug("Nie istnieje dodaję...")
                return@let applicantMapper.applicantDtoToApplicantMapper(it)
            }
        }
        logger.debug("zapisuję aplikanta")
        applicantRepository.save(applicant)
        logger.debug("Zapisałem aplikanta")
        if (!applicant.applications.any { it.irkId == application.irkId }) {
            application.applicant = applicant
            applicant.applications.add(application)
        }
        logger.debug("zapisuję aplikację")
        applicationRepository.save(application)
        if (!applicationRepository.existsByImportIdAndIrkId(importId, application.irkId)) {
            import.applications.add(application)
            application.import = import
        }
        logger.debug("zwiększam liczbę zaimportowanych")
        import.importProgress!!.importedApplications++
        return application
    }

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun processPerson(
            application: Application,
            dateOfAddmision: Date,
            didacticCycleCode: String,
            indexPollCode: String,
            programmeCode: String,
            registration: String,
            stageCode: String,
            startDate: Date
    ): Person {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw IllegalStateException("Nie ma aktywnej transakcji")
        }
        val personAndAssignedNumber = personService.processPerson(
                application = application,
                dateOfAddmision = dateOfAddmision,
                didacticCycleCode = didacticCycleCode,
                indexPoolCode = indexPollCode,
                programmeCode = programmeCode,
                registration = registration,
                stageCode = stageCode,
                startDate = startDate
        )
        personAndAssignedNumber.let { pair ->
            application.applicant!!.usosId = pair.first.id
            application.applicant!!.assignedIndexNumber = pair.second
        }
        application.importError = null
        application.stackTrace = null
        application.applicationImportStatus = ApplicationImportStatus.IMPORTED
        if (setAsAccepted) {
            irkService.completeImmatriculation(application.irkId)
        }
        applicantRepository.save(application.applicant!!)
        applicationRepository.save(application)
        return personAndAssignedNumber.first
    }

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun setSaveComplete(importId: Long) {
        val importProgress = importProgressRepository.getOne(importId)
        importProgress.importStatus = ImportStatus.COMPLETE
    }

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun handleSaveJdbcException(e: JDBCException, application: Application, importId: Long) {
        val import = importRepository.getOne(importId)
        application.applicationImportStatus = ApplicationImportStatus.ERROR
        application.importError = "${e.javaClass.simpleName}: ${e.message} \n Error code: ${e.errorCode} " +
                "Sql exception: ${e.sqlException} \n " +
                "Sql: ${e.sql} \n " +
                "Sql state: ${e.sqlState}"
        application.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
        import.importProgress!!.saveErrors++
        applicantRepository.save(application.applicant!!)
        applicationRepository.save(application)
    }

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun handleSaveException(e: Exception, application: Application, importId: Long) {
        val import = importRepository.getOne(importId)
        if (e.cause is JDBCException) {
            val ex: JDBCException = e.cause as JDBCException
            application.importError = "${e.javaClass.simpleName}: ${e.message} \n Error code: ${ex.errorCode} " +
                    "Sql exception: ${ex.sqlException} \n " +
                    "Sql: ${ex.sql} \n " +
                    "Sql state: ${ex.sqlState} "
        } else {
            application.importError = "${e.javaClass.simpleName}: ${e.message}"
        }
        application.applicationImportStatus = ApplicationImportStatus.ERROR
        application.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
        import.importProgress!!.saveErrors++
        applicantRepository.save(application.applicant!!)
        applicationRepository.save(application)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun incrementProgress(importId: Long) {
        val importProgress = importProgressRepository.getOne(importId)
        importProgress.savedApplicants++
    }

    @Transactional(rollbackFor = [Exception::class, RuntimeException::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun processPersons(
            importId: Long,
            dateOfAddmision: Date,
            didacticCycleCode: String,
            indexPoolCode: String,
            programmeCode: String,
            registration: String,
            stageCode: String,
            startDate: Date
    ) {
        val applicationsPage: Stream<Application> = applicationRepository.getAllByImportIdAndApplicationImportStatus(importId)
        applicationsPage.use {
            applicationsPage.forEach {
                try {
                    val person = self.processPerson(
                            application = it,
                            dateOfAddmision = dateOfAddmision,
                            didacticCycleCode = didacticCycleCode,
                            indexPollCode = indexPoolCode,
                            programmeCode = programmeCode,
                            registration = registration,
                            stageCode = stageCode,
                            startDate = startDate
                    )
                    self.incrementProgress(importId)
                    oracleEntityManager.detach(person)
                } catch (e: JDBCException) {
                    self.handleSaveJdbcException(e, it, importId)
                } catch (e: Exception) {
                    self.handleSaveException(e, it, importId)
                }
            }
        }
    }
}