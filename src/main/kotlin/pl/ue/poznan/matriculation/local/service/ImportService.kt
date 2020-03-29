package pl.ue.poznan.matriculation.local.service

import lombok.extern.slf4j.Slf4j
import org.hibernate.exception.SQLGrammarException
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
import pl.ue.poznan.matriculation.local.repo.ApplicantRepository
import pl.ue.poznan.matriculation.local.repo.ApplicationRepository
import pl.ue.poznan.matriculation.local.repo.ImportProgressRepository
import pl.ue.poznan.matriculation.local.repo.ImportRepository
import pl.ue.poznan.matriculation.oracle.repo.ProgrammeRepository
import pl.ue.poznan.matriculation.oracle.service.PersonService
import java.text.SimpleDateFormat
import java.util.*


@Service
@Slf4j
class ImportService(
        private val irkService: IrkService,
        private val importRepository: ImportRepository,
        private val personService: PersonService,
        private val applicantRepository: ApplicantRepository,
        private val programmeRepository: ProgrammeRepository,
        private val applicationService: ApplicationService,
        private val importProgressRepository: ImportProgressRepository,
        private val applicationRepository: ApplicationRepository
) {

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
            throw ImportCreationException("Import tego programu już istnieje")
        }
        if (!programmeRepository.existsById(programmeCode)) {
            throw ImportCreationException("Wybrany program nie istnieje w USOSie")
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

    @Transactional(rollbackFor = [ImportException::class], propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun importApplications(import: Import) {
        import.importProgress!!.importStatus = ImportStatus.STARTED
        importProgressRepository.save(import.importProgress!!)
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
                    import.importProgress!!.totalCount = page.count
                    importProgressRepository.save(import.importProgress!!)
                    set = false
                }
                page.results.forEach {
                    applicationService.processApplicant(import, it)
                }
                hasNext = page.next != null
                currentPage++
            } while (hasNext)
            import.importProgress!!.importStatus = ImportStatus.IMPORTED
            importProgressRepository.save(import.importProgress!!)
            importRepository.save(import)
        } catch (e: java.lang.Exception) {
            throw ImportException(import.id!!, e.message, e)
        }

    }

    fun getImport(importId: Long): Import {
        return importRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono imortu")
    }

    fun getProgress(importId: Long): ImportProgress {
        return importProgressRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono imortu")
    }

    fun setError(importId: Long, errorMessage: String) {
        val importProgress = importProgressRepository.getOne(importId)
        importProgress.error = errorMessage
        importProgressRepository.save(importProgress)
    }

    fun savePersons(import: Import) {
        import.importProgress!!.apply {
            importStatus = ImportStatus.SAVING
            saveErrors = 0
        }
        importProgressRepository.save(import.importProgress!!)
        import.applications.filter {
            it.applicationImportStatus == ApplicationImportStatus.NOT_IMPORTED
                    || it.applicationImportStatus == ApplicationImportStatus.ERROR
        }.forEach {
            try {
                val personId = personService.processPerson(import, it)
                it.applicant!!.usosId = personId
                it.importError = null
                it.stackTrace = null
                it.applicationImportStatus = ApplicationImportStatus.IMPORTED
            } catch (e: SQLGrammarException) {
                it.applicationImportStatus = ApplicationImportStatus.ERROR
                it.importError = "${e.javaClass.simpleName}: ${e.message} ${e.sqlException}"
                it.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
                import.importProgress!!.saveErrors++
            } catch (e: Exception) {
                it.applicationImportStatus = ApplicationImportStatus.ERROR
                it.importError = "${e.javaClass.simpleName}: ${e.message}"
                it.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
                import.importProgress!!.saveErrors++
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
                ?: throw ImportNotFoundException("Nie znaleziono imortu")
        if (import.importProgress!!.importStatus != ImportStatus.PENDING) {
            throw ImportException(importId, "Import już się rozpoczął")
        }
        return import
    }

    fun getImportForPersonSave(importId: Long): Import {
        val import: Import = importRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono imortu")
        if (import.importProgress!!.importStatus != ImportStatus.IMPORTED && import.importProgress!!.importStatus != ImportStatus.COMPLETE) {
            throw ImportStartException(importId, "Zły stan importu")
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