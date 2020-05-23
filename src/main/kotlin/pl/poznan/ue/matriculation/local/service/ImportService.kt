package pl.poznan.ue.matriculation.local.service

import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.*
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.domain.import.ImportProgress
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.repo.DidacticCycleRepository
import pl.poznan.ue.matriculation.oracle.repo.ProgrammeRepository
import java.text.SimpleDateFormat
import java.util.*


@Service
class ImportService(
        private val importRepository: ImportRepository,
        private val programmeRepository: ProgrammeRepository,
        private val importProgressRepository: ImportProgressRepository,
        private val didacticCycleRepository: DidacticCycleRepository
) {

    fun create(
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

    fun get(importId: Long): Import {
        return importRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono imoportu.")
    }

    fun getProgress(importId: Long): ImportProgress {
        return importProgressRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono importu.")
    }

    fun setError(importId: Long, errorMessage: String) {
        val importProgress = importProgressRepository.getOne(importId)
        importProgress.error = errorMessage
        importProgressRepository.save(importProgress)
    }

    fun getAll(pageable: org.springframework.data.domain.Pageable): Page<Import> {
        return importRepository.findAll(pageable)
    }

    fun getForApplicantImport(importId: Long): Import {
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

    fun getForPersonSave(importId: Long): Import {
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

    fun delete(importId: Long) {
        try {
            importRepository.deleteById(importId)
        } catch (e: Exception) {
            throw ImportDeleteException("Nie Można usunąć importu: ${e.message}")
        }
    }
}