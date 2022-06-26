package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class UidSearchStartConditions : IStartConditions {
    override fun canStart(import: Import): StateTransitionResult {
        when (import.importStatus) {
            ImportStatus.ARCHIVED -> return StateTransitionFailure("Import został zarchiwizowany.")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.IMPORTED,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.CHECKING_POTENTIAL_DUPLICATES,
            ImportStatus.SEARCHING_UIDS -> return StateTransitionFailure("Zły stan importu.")
            ImportStatus.ERROR,
            ImportStatus.COMPLETE,
            ImportStatus.COMPLETED_WITH_ERRORS -> {
                val totalCount = import.totalCount
                    ?: return StateTransitionFailure("Liczba aplikantów to null")
                if (totalCount < 1) {
                    return StateTransitionFailure("Liczba aplikantów wynosi 0")
                }
                if (import.savedApplicants != totalCount) {
                    return StateTransitionFailure("Nie wszyscy kandydaci zostali zapisani")
                }
                if (import.importedUids == totalCount) {
                    return StateTransitionFailure("Wszystkie UIDy są już znalezione")
                }
            }
        }
        return StateTransitionSuccess
    }
}
