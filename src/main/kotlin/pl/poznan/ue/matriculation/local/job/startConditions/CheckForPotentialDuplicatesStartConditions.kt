package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class CheckForPotentialDuplicatesStartConditions : IStartConditions {
    override fun canStart(import: Import): StateTransitionResult {
        when (import.importStatus) {
            ImportStatus.ARCHIVED -> return StateTransitionFailure("Import został zarchiwizowany.")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.ERROR,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.COMPLETE,
            ImportStatus.COMPLETED_WITH_ERRORS,
            ImportStatus.SEARCHING_UIDS,
            ImportStatus.CHECKING_POTENTIAL_DUPLICATES -> return StateTransitionFailure("Zły stan importu. ${import.importStatus}")
            ImportStatus.IMPORTED -> {
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
