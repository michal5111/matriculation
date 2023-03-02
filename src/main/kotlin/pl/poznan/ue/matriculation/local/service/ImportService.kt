package pl.poznan.ue.matriculation.local.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.server.ResponseStatusException
import pl.poznan.ue.matriculation.exception.*
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ImportDto
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.repo.DidacticCycleRepository
import pl.poznan.ue.matriculation.oracle.repo.ProgrammeRepository


@Service
@Transactional(rollbackFor = [Exception::class])
class ImportService(
    private val importRepository: ImportRepository,
    private val programmeRepository: ProgrammeRepository,
    private val didacticCycleRepository: DidacticCycleRepository
) {

    private val logger = LoggerFactory.getLogger(ImportService::class.java)

    fun create(
        importDto: ImportDto
    ): Import {
        val import = Import(
            dateOfAddmision = importDto.dateOfAddmision!!,
            didacticCycleCode = importDto.didacticCycleCode!!,
            indexPoolCode = importDto.indexPoolCode!!,
            indexPoolName = importDto.indexPoolName!!,
            programmeCode = importDto.programmeCode!!,
            programmeForeignId = importDto.programmeForeignId!!,
            programmeForeignName = importDto.programmeForeignName!!,
            registration = importDto.registration!!,
            startDate = importDto.startDate!!,
            stageCode = importDto.stageCode!!,
            dataSourceId = importDto.dataSourceId!!,
            dataSourceName = importDto.dataSourceName!!,
            additionalProperties = importDto.additionalProperties,
        )
        validateImport(import)
        return importRepository.save(import)
    }

    fun validateImport(import: Import) {
        if (!UserService.checkDataSourcePermission(import.dataSourceId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
//        if (importRepository.existsByProgrammeForeignIdAndRegistrationAndStageCodeAndDidacticCycleCode(
//                programmeForeignId,
//                registration,
//                stageCode,
//                didacticCycleCode
//            )
//        ) {
//            throw ImportCreationException("Import tego programu już istnieje.")
//        }
        if (!programmeRepository.existsById(import.programmeCode)) {
            throw ImportCreationException("Wybrany program nie istnieje w USOSie.")
        }
        if (!didacticCycleRepository.existsById(import.didacticCycleCode)) {
            throw ImportCreationException("Podany cykl dydaktyczny nie istnieje.")
        }
        if (import.dateOfAddmision < import.startDate) {
            throw ImportCreationException("Data przyjęcia na program musi być mniejsza bądź równa dacie rozpoczęcia.")
        }
    }

    fun findById(importId: Long): Import {
        return importRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
    }

    fun get(importId: Long): Import {
        if (TransactionSynchronizationManager.isActualTransactionActive().not()) {
            throw IllegalStateException("No active transaction")
        }
        return importRepository.getErrorAndStatusById(importId)
    }

    fun getAll(pageable: org.springframework.data.domain.Pageable): Page<Import> {
        return importRepository.findAll(pageable)
    }

    fun delete(importId: Long) {
        try {
            val import = importRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
            when (import.importStatus) {
                ImportStatus.ARCHIVED -> throw ImportException(importId, "Import został zarchiwizowany.")
                ImportStatus.STARTED,
                ImportStatus.SAVING,
                ImportStatus.COMPLETE,
                ImportStatus.COMPLETED_WITH_ERRORS,
                ImportStatus.SENDING_NOTIFICATIONS,
                ImportStatus.CHECKING_POTENTIAL_DUPLICATES,
                ImportStatus.SEARCHING_UIDS -> throw ImportStartException(importId, "Zły stan importu.")

                ImportStatus.IMPORTED,
                ImportStatus.ERROR,
                ImportStatus.PENDING -> {
                    if (import.savedApplicants != 0) {
                        throw IllegalStateException("Liczba sapisanych kandydatów jest większa od 0")
                    }
                    importRepository.deleteById(importId)
                }
            }
        } catch (e: Exception) {
            logger.error("Błąd przy usuwaniu importu", e)
            throw ImportDeleteException("Nie Można usunąć importu", e)
        }
    }

    fun save(import: Import): Import {
        return importRepository.save(import)
    }

    fun setError(importId: Long, error: String, stackTrace: String? = null) =
        importRepository.getErrorAndStatusById(importId).apply {
            this.error = error
            this.stackTrace = stackTrace
            importStatus = ImportStatus.ERROR
        }

    fun setImportStatus(importStatus: ImportStatus, importId: Long) =
        importRepository.findByIdOrNull(importId)?.apply {
            this.importStatus = importStatus
        }

    fun updateImport(importDto: ImportDto): Import {
        val import = importRepository.findByIdOrNull(importDto.id)
            ?: throw ImportNotFoundException("Nie znaleziono importu")
        if (import.importStatus !in arrayOf(
                ImportStatus.PENDING,
                ImportStatus.IMPORTED,
                ImportStatus.ERROR,
                ImportStatus.COMPLETED_WITH_ERRORS
            )
        ) {
            throw IllegalStateException("Nie można edytować importu w tym stanie")
        }
        validateImport(import)
        return importRepository.save(import)
    }
}
