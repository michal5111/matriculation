package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class SendNotificationsStartConditions : IStartConditions {
    override fun canStart(import: Import): StateTransitionResult {
        when (import.importStatus) {
            ImportStatus.ARCHIVED -> return StateTransitionFailure("Import został zarchiwizowany")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.IMPORTED,
            ImportStatus.SAVING,
            ImportStatus.SEARCHING_UIDS,
            ImportStatus.CHECKING_POTENTIAL_DUPLICATES,
            ImportStatus.SENDING_NOTIFICATIONS -> return StateTransitionFailure("Zły stan importu.")
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
                if (import.notificationsSend == totalCount) {
                    return StateTransitionFailure("Wszystkie powiadomienia są już wysłane")
                }
            }
        }
        return StateTransitionSuccess
    }
}
