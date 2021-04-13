package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.ImportStartException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class SendNotificationsStartConditions : IStartConditions {
    override fun canStart(import: Import) {
        when (import.importProgress.importStatus) {
            ImportStatus.ARCHIVED -> throw ImportException(import.id!!, "Import został zarchiwizowany")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.IMPORTED,
            ImportStatus.SAVING,
            ImportStatus.SEARCHING_UIDS,
            ImportStatus.SENDING_NOTIFICATIONS -> throw ImportStartException(import.id!!, "Zły stan importu.")
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
                if (import.importProgress.importedUids != totalCount) {
                    throw ImportStartException(import.id!!, "Nie wszystkie UIDy są znalezione")
                }
                if (import.importProgress.notificationsSend == totalCount) {
                    throw ImportStartException(import.id!!, "Wszystkie powiadomienia są już wysłane")
                }
            }
        }
    }
}