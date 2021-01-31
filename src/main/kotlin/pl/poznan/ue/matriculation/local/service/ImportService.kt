package pl.poznan.ue.matriculation.local.service

import org.slf4j.LoggerFactory
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
import java.util.*


@Service
class ImportService(
        private val importRepository: ImportRepository,
        private val programmeRepository: ProgrammeRepository,
        private val importProgressRepository: ImportProgressRepository,
        private val didacticCycleRepository: DidacticCycleRepository
) {

    private val logger = LoggerFactory.getLogger(ImportService::class.java)

    fun create(
        programmeCode: String,
        programmeForeignId: String,
        registration: String,
        indexPoolCode: String,
        startDate: Date,
        dateOfAddmision: Date,
        stageCode: String,
        didacticCycleCode: String,
        dataSourceType: String,
        dataFile: String?
    ): Import {
        if (importRepository.existsByProgrammeForeignIdAndRegistrationAndStageCode(programmeForeignId, registration, stageCode)) {
            throw ImportCreationException("Import tego programu już istnieje.")
        }
        if (!programmeRepository.existsById(programmeCode)) {
            throw ImportCreationException("Wybrany program nie istnieje w USOSie.")
        }
        if (!didacticCycleRepository.existsById(didacticCycleCode)) {
            throw ImportCreationException("Podany cykl dydaktyczny nie istnieje.")
        }
        val import = Import(
            dateOfAddmision = dateOfAddmision,
            didacticCycleCode = didacticCycleCode,
            indexPoolCode = indexPoolCode,
            programmeCode = programmeCode,
            programmeForeignId = programmeForeignId,
            registration = registration,
            startDate = startDate,
            stageCode = stageCode,
            dataSourceId = dataSourceType,
            dataFile = dataFile?.let {
                Base64.getDecoder().decode(dataFile)
            }
        )
        return importRepository.save(import)
    }

    fun setImportStatus(importStatus: ImportStatus, importId: Long) {
        importProgressRepository.setStatus(importStatus, importId)
    }

    fun prepareForSaving(importId: Long) {
        val import = getForPersonSave(importId)
        import.importProgress.saveErrors = 0
        import.importProgress.importStatus = ImportStatus.SAVING
        importRepository.save(import)
    }

    fun prepareForImporting(importId: Long) {
        val import = getForApplicantImport(importId)
        import.importProgress.importStatus = ImportStatus.STARTED
        import.importProgress.importedApplications = 0
        importRepository.save(import)
    }

    fun prepareForSearchingUids(importId: Long) {
        val import = getForUidsSearch(importId)
        import.importProgress.importedUids = 0
        import.importProgress.importStatus = ImportStatus.SEARCHING_UIDS
        importRepository.save(import)
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
        importProgressRepository.setError(errorMessage, importId)
    }

    fun getAll(pageable: org.springframework.data.domain.Pageable): Page<Import> {
        return importRepository.findAll(pageable)
    }

    fun getForApplicantImport(importId: Long): Import {
        val import: Import = importRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono importu.")
        when (import.importProgress.importStatus) {
            ImportStatus.ARCHIVED -> throw ImportException(importId, "Import został zarchiwizowany.")
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.SEARCHING_UIDS,
            ImportStatus.SENDING_NOTIFICATIONS -> throw ImportException(importId, "Import już się rozpoczął.")
            ImportStatus.COMPLETE -> throw ImportException(importId, "Import zakończony")
            ImportStatus.IMPORTED,
            ImportStatus.PENDING,
            ImportStatus.COMPLETED_WITH_ERRORS,
            ImportStatus.ERROR -> return if (import.importProgress.savedApplicants != import.importProgress.totalCount) import
            else throw ImportException(importId, "Wszyscy sa już zapisani")
            else -> throw ImportException(importId, "Nieznany stan")
        }
    }

    fun getForPersonSave(importId: Long): Import {
        val import: Import = importRepository.findByIdOrNull(importId)
                ?: throw ImportNotFoundException("Nie znaleziono importu.")
        val importProgress = import.importProgress
        when (importProgress.importStatus) {
            ImportStatus.ARCHIVED -> throw ImportException(importId, "Import został zarchiwizowany.")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.ERROR,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.SEARCHING_UIDS -> throw ImportStartException(importId, "Zły stan importu.")
            ImportStatus.IMPORTED,
            ImportStatus.COMPLETE,
            ImportStatus.COMPLETED_WITH_ERRORS -> {
                val totalCount = importProgress.totalCount
                    ?: throw ImportStartException(importId, "Liczba aplikantów to null")
                if (totalCount < 1 || totalCount == importProgress.savedApplicants) {
                    throw ImportStartException(importId, "Liczba aplikantów wynosi 0 lub wszyscy są już zapisani")
                }
                return import
            }
            else -> throw IllegalStateException("Nieznany stan")
        }
    }

    fun delete(importId: Long) {
//        val import = importRepository.getOne(importId)
//        if (import.applications.size > 0) {
//            if (import.importProgress!!.importStatus != ImportStatus.IMPORTED) {
//                throw ImportDeleteException("Nie można usunąć importu.")
//            }
//            import.applications.filter { application ->
//                !application.applicant!!.applications.filter {
//                    it.id != application.id
//                }.any()
//            }
//        }
        try {
            val import = importRepository.getOne(importId)
            when (import.importProgress.importStatus) {
                ImportStatus.ARCHIVED -> throw ImportException(importId, "Import został zarchiwizowany.")
                ImportStatus.STARTED,
                ImportStatus.SAVING,
                ImportStatus.IMPORTED,
                ImportStatus.COMPLETE,
                ImportStatus.COMPLETED_WITH_ERRORS,
                ImportStatus.SENDING_NOTIFICATIONS,
                ImportStatus.SEARCHING_UIDS -> throw ImportStartException(importId, "Zły stan importu.")
                ImportStatus.ERROR,
                ImportStatus.PENDING -> {
                    if (import.importProgress.importedApplications != 0) {
                        throw IllegalStateException("Liczba kandydatów jest większa od 0")
                    }
                    importRepository.deleteById(importId)
                    logger.info("Usuwam import $importId")
                }
                else -> throw IllegalStateException("Nieznany stan")
            }
        } catch (e: Exception) {
            logger.error("Błąd przy usuwaniu importu", e)
            throw ImportDeleteException("Nie Można usunąć importu: ${e.message}")
        }
    }

    fun getForUidsSearch(importId: Long): Import {
        val import: Import = importRepository.findByIdOrNull(importId)
            ?: throw ImportNotFoundException("Nie znaleziono importu.")
        val importProgress = import.importProgress
        when (importProgress.importStatus) {
            ImportStatus.ARCHIVED -> throw ImportException(importId, "Import został zarchiwizowany.")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.IMPORTED,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.SEARCHING_UIDS -> throw ImportStartException(importId, "Zły stan importu.")
            ImportStatus.ERROR,
            ImportStatus.COMPLETE,
            ImportStatus.COMPLETED_WITH_ERRORS -> {
                val totalCount = importProgress.totalCount
                    ?: throw ImportStartException(importId, "Liczba aplikantów to null")
                if (totalCount < 1) {
                    throw ImportStartException(importId, "Liczba aplikantów wynosi 0")
                }
                if (importProgress.savedApplicants != totalCount) {
                    throw ImportStartException(importId, "Nie wszyscy kandydaci zostali zapisani")
                }
                return import
            }
            else -> throw IllegalStateException("Nieznany stan")
        }
    }

    fun save(import: Import): Import {
        return importRepository.save(import)
    }
}