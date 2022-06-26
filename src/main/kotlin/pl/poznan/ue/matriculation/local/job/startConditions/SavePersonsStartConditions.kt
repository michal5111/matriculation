package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class SavePersonsStartConditions : IStartConditions {
    override fun canStart(import: Import): StateTransitionResult {
        when (import.importStatus) {
            ImportStatus.ARCHIVED -> return StateTransitionFailure("Import został zarchiwizowany.")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.ERROR,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.CHECKING_POTENTIAL_DUPLICATES,
            ImportStatus.SEARCHING_UIDS -> return StateTransitionFailure("Zły stan importu. ${import.importStatus} ${import.error.orEmpty()}")
            ImportStatus.IMPORTED,
            ImportStatus.COMPLETE,
            ImportStatus.COMPLETED_WITH_ERRORS -> {
                val totalCount = import.totalCount
                    ?: return StateTransitionFailure("Liczba aplikantów to null")
                if (totalCount < 1 || totalCount == import.savedApplicants) {
                    return StateTransitionFailure("Liczba aplikantów wynosi 0 lub wszyscy są już zapisani")
                }
            }
        }
        return StateTransitionSuccess
    }
}
