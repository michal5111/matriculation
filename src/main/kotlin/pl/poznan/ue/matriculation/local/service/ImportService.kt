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
        programmeForeignName: String,
        registration: String,
        indexPoolCode: String,
        indexPoolName: String,
        startDate: Date,
        dateOfAddmision: Date,
        stageCode: String,
        didacticCycleCode: String,
        dataSourceType: String,
        additionalProperties: Map<String, Any>? = null
    ): Import {
        logger.info("additionalProperties $additionalProperties")
        if (!UserService.checkDataSourcePermission(dataSourceType)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
//        if (importRepository.existsByProgrammeForeignIdAndRegistrationAndStageCodeAndDidacticCycleCode(
//                programmeForeignId,
//                registration,
//                stageCode,
//                didacticCycleCode
//            )
//        ) {
//            throw ImportCreationException("Import tego programu ju?? istnieje.")
//        }
        if (!programmeRepository.existsById(programmeCode)) {
            throw ImportCreationException("Wybrany program nie istnieje w USOSie.")
        }
        if (!didacticCycleRepository.existsById(didacticCycleCode)) {
            throw ImportCreationException("Podany cykl dydaktyczny nie istnieje.")
        }
        if (dateOfAddmision < startDate) {
            throw ImportCreationException("Data przyj??cia na program musi by?? mniejsza b??d?? r??wna dacie rozpocz??cia.")
        }
        val import = Import(
            dateOfAddmision = dateOfAddmision,
            didacticCycleCode = didacticCycleCode,
            indexPoolCode = indexPoolCode,
            indexPoolName = indexPoolName,
            programmeCode = programmeCode,
            programmeForeignId = programmeForeignId,
            programmeForeignName = programmeForeignName,
            registration = registration,
            startDate = startDate,
            stageCode = stageCode,
            dataSourceId = dataSourceType,
            additionalProperties = additionalProperties,
        )
        return importRepository.save(import)
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
            val import = importRepository.getById(importId)
            when (import.importStatus) {
                ImportStatus.ARCHIVED -> throw ImportException(importId, "Import zosta?? zarchiwizowany.")
                ImportStatus.STARTED,
                ImportStatus.SAVING,
                ImportStatus.COMPLETE,
                ImportStatus.COMPLETED_WITH_ERRORS,
                ImportStatus.SENDING_NOTIFICATIONS,
                ImportStatus.CHECKING_POTENTIAL_DUPLICATES,
                ImportStatus.SEARCHING_UIDS -> throw ImportStartException(importId, "Z??y stan importu.")
                ImportStatus.IMPORTED,
                ImportStatus.ERROR,
                ImportStatus.PENDING -> {
                    if (import.savedApplicants != 0) {
                        throw IllegalStateException("Liczba sapisanych kandydat??w jest wi??ksza od 0")
                    }
                    importRepository.deleteById(importId)
                }
            }
        } catch (e: Exception) {
            logger.error("B????d przy usuwaniu importu", e)
            throw ImportDeleteException("Nie Mo??na usun???? importu", e)
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
        importRepository.getById(importId).apply {
            this.importStatus = importStatus
        }
}
