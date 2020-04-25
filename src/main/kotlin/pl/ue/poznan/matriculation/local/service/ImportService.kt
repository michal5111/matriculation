package pl.ue.poznan.matriculation.local.service

import org.hibernate.JDBCException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.ue.poznan.matriculation.exception.*
import pl.ue.poznan.matriculation.irk.service.IrkService
import pl.ue.poznan.matriculation.local.domain.enum.ApplicationImportStatus
import pl.ue.poznan.matriculation.local.domain.enum.ImportStatus
import pl.ue.poznan.matriculation.local.domain.import.Import
import pl.ue.poznan.matriculation.local.domain.import.ImportProgress
import pl.ue.poznan.matriculation.local.domain.import.ImportProgressDto
import pl.ue.poznan.matriculation.local.repo.ApplicantRepository
import pl.ue.poznan.matriculation.local.repo.ApplicationRepository
import pl.ue.poznan.matriculation.local.repo.ImportProgressRepository
import pl.ue.poznan.matriculation.local.repo.ImportRepository
import pl.ue.poznan.matriculation.oracle.repo.DidacticCycleRepository
import pl.ue.poznan.matriculation.oracle.repo.ProgrammeRepository
import pl.ue.poznan.matriculation.oracle.service.PersonService
import java.text.SimpleDateFormat
import java.util.*


@Service
class ImportService(
        private val irkService: IrkService,
        private val importRepository: ImportRepository,
        private val personService: PersonService,
        private val applicantRepository: ApplicantRepository,
        private val programmeRepository: ProgrammeRepository,
        private val applicationService: ApplicationService,
        private val importProgressRepository: ImportProgressRepository,
        private val applicationRepository: ApplicationRepository,
        private val didacticCycleRepository: DidacticCycleRepository
) {

    @Value("\${pl.ue.poznan.matriculation.setAsAccepted}")
    private var setAsAccepted: Boolean = false

    fun createImport(
            programmeCode: String,
            registration: String,
            indexPoolCode: String,
            startDate: String,
            dateOfAddmision: String,
            stageCode: String,
            didacticCycleCode: String
    ): Import {
        if (importRepository.existsByProgrammeCodeAndRegistration(programmeCode, registration)) {
            throw ImportCreationException("Import tego programu już istnieje.")
        }
        if (!programmeRepository.existsById(programmeCode)) {
            throw ImportCreationException("Wybrany program nie istnieje w USOSie.")
        }
        if (!didacticCycleRepository.existsById(didacticCycleCode)) {
            throw ImportCreationException("Podany cykl dydaktyczny nie istnieje.")
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val import = Import(
                dateOfAddmision = simpleDateFormat.parse(dateOfAddmision),
                didacticCycleCode = didacticCycleCode,
                indexPoolCode = indexPoolCode,
                programmeCode = programmeCode,
                registration = registration,
                startDate = simpleDateFormat.parse(startDate),
                stageCode = stageCode
        ).apply {
            importProgress = ImportProgress(
                    import = this
            )
        }
        return importRepository.save(import)
    }

    fun setImportStatus(importStatus: ImportStatus, importId: Long) {
        val importProgress = getProgress(importId)
        importProgress.importStatus = importStatus
        importProgressRepository.save(importProgress)
    }

    @Transactional(rollbackFor = [ImportException::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun importApplications(importId: Long) {
        val import = getImport(importId)
        var currentPage = 1
        var hasNext: Boolean
        var set = true
        try {
            do {
                val page = irkService.getApplications(
                        admitted = true,
                        paid = true,
                        programme = import.programmeCode,
                        registration = import.registration,
                        pageNumber = currentPage
                )
                if (set) {
                    if (page.count == 0) {
                        throw ImportException(import.id!!, "Liczba kandydatów wynosi 0!")
                    }
                    import.importProgress!!.totalCount = page.count
                    importProgressRepository.save(import.importProgress!!)
                    set = false
                }
                page.results.forEach {
                    applicationService.processApplication(import, it)
                }
                hasNext = page.next != null
                currentPage++
            } while (hasNext)
            importRepository.save(import)
            import.importProgress!!.importStatus = ImportStatus.IMPORTED
            importProgressRepository.save(import.importProgress!!)
        } catch (e: java.lang.Exception) {
            throw ImportException(import.id!!, e.message, e)
        }

    }

    fun resetSaveErrors(importId: Long) {
        val importProgress = getProgress(importId)
        importProgress.saveErrors = 0
        importProgressRepository.save(importProgress)
    }

    fun resetImportedApplications(importId: Long) {
        val importProgress = getProgress(importId)
        importProgress.importedApplications = 0
        importProgressRepository.save(importProgress)
    }

    fun getImport(importId: Long): Import {
        return importRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono imoportu.")
    }

    fun getProgress(importId: Long): ImportProgress {
        return importProgressRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono importu.")
    }

    fun getProgressRest(importId: Long): ImportProgressDto {
        return importProgressRepository.getProgress(importId)
                ?: throw ImportNotFoundException("Nie znaleziono importu.")
    }

    fun setError(importId: Long, errorMessage: String) {
        val importProgress = importProgressRepository.getOne(importId)
        importProgress.error = errorMessage
        importProgressRepository.save(importProgress)
    }

    fun savePersons(importId: Long) {
        val import = getImport(importId)
        import.applications.filter {
            it.applicationImportStatus == ApplicationImportStatus.NOT_IMPORTED
                    || it.applicationImportStatus == ApplicationImportStatus.ERROR
        }.forEach {
            try {
                val personIdAndAssignedNumber = personService.processPerson(import, it)
                import.importProgress!!.savedApplicants++
                personIdAndAssignedNumber.let { pair ->
                    it.applicant!!.usosId = pair.first
                    it.applicant!!.assignedIndexNumber = pair.second
                }
                it.importError = null
                it.stackTrace = null
                it.applicationImportStatus = ApplicationImportStatus.IMPORTED
                if (setAsAccepted) {
                    irkService.completeImmatriculation(it.irkId)
                }
            } catch (e: JDBCException) {
                it.applicationImportStatus = ApplicationImportStatus.ERROR
                it.importError = "${e.javaClass.simpleName}: ${e.message} \n Error code: ${e.errorCode} " +
                        "Sql exception: ${e.sqlException} \n " +
                        "Sql: ${e.sql} \n " +
                        "Sql state: ${e.sqlState}"
                it.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
                import.importProgress!!.saveErrors++
            } catch (e: Exception) {
                if (e.cause is JDBCException) {
                    val ex: JDBCException = e.cause as JDBCException
                    it.applicationImportStatus = ApplicationImportStatus.ERROR
                    it.importError = "${e.javaClass.simpleName}: ${e.message} \n Error code: ${ex.errorCode} " +
                            "Sql exception: ${ex.sqlException} \n " +
                            "Sql: ${ex.sql} \n " +
                            "Sql state: ${ex.sqlState} "
                    it.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
                    import.importProgress!!.saveErrors++
                } else {
                    it.applicationImportStatus = ApplicationImportStatus.ERROR
                    it.importError = "${e.javaClass.simpleName}: ${e.message}"
                    it.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
                    import.importProgress!!.saveErrors++
                }
            }
            importProgressRepository.save(import.importProgress!!)
            applicantRepository.save(it.applicant!!)
            applicationRepository.save(it)
        }
        import.importProgress!!.importStatus = ImportStatus.COMPLETE
        importProgressRepository.save(import.importProgress!!)
    }

    fun getAllImports(pageable: org.springframework.data.domain.Pageable): Page<Import> {
        return importRepository.findAll(pageable)
    }

    fun getImportForApplicantImport(importId: Long): Import {
        val import: Import = importRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono importu.")
        if (import.importProgress!!.importStatus == ImportStatus.STARTED ||
                import.importProgress!!.importStatus == ImportStatus.SAVING) {
            throw ImportException(importId, "Import już się rozpoczął.")
        }
        if (import.importProgress!!.importStatus == ImportStatus.ARCHIVED) {
            throw ImportException(importId, "Import został zarchiwizowany.")
        }
        return import
    }

    fun getImportForPersonSave(importId: Long): Import {
        val import: Import = importRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono importu.")
        if (import.importProgress!!.importStatus != ImportStatus.IMPORTED && import.importProgress!!.importStatus != ImportStatus.COMPLETE) {
            throw ImportStartException(importId, "Zły stan importu.")
        }
        if (import.importProgress!!.importStatus == ImportStatus.ARCHIVED) {
            throw ImportException(importId, "Import został zarchiwizowany.")
        }
        return import
    }

    fun deleteImport(importId: Long) {
        try {
            importRepository.deleteById(importId)
        } catch (e: Exception) {
            throw ImportDeleteException("Nie Można usunąć importu: ${e.message}")
        }
    }
}