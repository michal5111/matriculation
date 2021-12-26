package pl.poznan.ue.matriculation.local.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import pl.poznan.ue.matriculation.exception.*
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.domain.import.ImportProgress
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.repo.DidacticCycleRepository
import pl.poznan.ue.matriculation.oracle.repo.ProgrammeRepository
import java.util.*


@Service
@Transactional
class ImportService(
    private val importRepository: ImportRepository,
    private val programmeRepository: ProgrammeRepository,
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
        dataFile: String? = null
    ): Import {
        if (importRepository.existsByProgrammeForeignIdAndRegistrationAndStageCodeAndDidacticCycleCode(
                programmeForeignId,
                registration,
                stageCode,
                didacticCycleCode
            )
        ) {
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

    fun get(importId: Long): Import {
        if (TransactionSynchronizationManager.isActualTransactionActive().not()) {
            throw IllegalStateException("No active transaction")
        }
        return importRepository.getErrorAndStatusById(importId)
    }

    fun getProgress(importId: Long): ImportProgress {
        return importRepository.findProgressById(importId)
            ?: throw ImportNotFoundException("Nie znaleziono importu.")
    }

    fun getAll(pageable: org.springframework.data.domain.Pageable): Page<Import> {
        return importRepository.findAll(pageable)
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
            val import = importRepository.getById(importId)
            when (import.importStatus) {
                ImportStatus.ARCHIVED -> throw ImportException(importId, "Import został zarchiwizowany.")
                ImportStatus.STARTED,
                ImportStatus.SAVING,
                ImportStatus.IMPORTED,
                ImportStatus.COMPLETE,
                ImportStatus.COMPLETED_WITH_ERRORS,
                ImportStatus.SENDING_NOTIFICATIONS,
                ImportStatus.CHECKING_POTENTIAL_DUPLICATES,
                ImportStatus.SEARCHING_UIDS -> throw ImportStartException(importId, "Zły stan importu.")
                ImportStatus.ERROR,
                ImportStatus.PENDING -> {
                    if (import.importedApplications != 0) {
                        throw IllegalStateException("Liczba kandydatów jest większa od 0")
                    }
                    importRepository.deleteById(importId)
                    logger.info("Usuwam import $importId")
                }
            }
        } catch (e: Exception) {
            logger.error("Błąd przy usuwaniu importu", e)
            throw ImportDeleteException("Nie Można usunąć importu: ${e.message}")
        }
    }

    fun save(import: Import): Import {
        return importRepository.save(import)
    }

    fun setError(importId: Long, s: String) = importRepository.getWithErrorById(importId).apply {
        error = s
    }.also {
        importRepository.save(it)
    }

    fun setImportStatus(importStatus: ImportStatus, importId: Long) =
        importRepository.getWithImportStatusById(importId).apply {
            this.importStatus = importStatus
        }.also {
            importRepository.save(it)
        }
}
