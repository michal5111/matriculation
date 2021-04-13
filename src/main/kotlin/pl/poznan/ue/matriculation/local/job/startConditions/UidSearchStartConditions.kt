package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.ImportStartException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class UidSearchStartConditions : IStartConditions {
    override fun canStart(import: Import) {
        when (import.importProgress.importStatus) {
            ImportStatus.ARCHIVED -> throw ImportException(import.id!!, "Import został zarchiwizowany.")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.IMPORTED,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.SEARCHING_UIDS -> throw ImportStartException(import.id!!, "Zły stan importu.")
            ImportStatus.ERROR,
            ImportStatus.COMPLETE,
            ImportStatus.COMPLETED_WITH_ERRORS -> {
                val totalCount = import.importProgress.totalCount
                    ?: throw ImportStartException(import.id!!, "Liczba aplikantów to null")
                if (totalCount < 1) {
                    throw ImportStartException(import.id!!, "Liczba aplikantów wynosi 0")
                }
                if (import.importProgress.savedApplicants != totalCount) {
                    throw ImportStartException(import.id!!, "Nie wszyscy kandydaci zostali zapisani")
                }
                if (import.importProgress.importedUids == totalCount) {
                    throw ImportStartException(import.id!!, "Wszystkie UIDy są już znalezione")
                }
            }
        }
    }
}