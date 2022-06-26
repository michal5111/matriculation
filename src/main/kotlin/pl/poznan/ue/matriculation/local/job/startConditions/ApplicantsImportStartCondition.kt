package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class ApplicantsImportStartCondition : IStartConditions {
    override fun canStart(import: Import): StateTransitionResult {
        when (import.importStatus) {
            ImportStatus.ARCHIVED -> return StateTransitionFailure("Import został zarchiwizowany.")
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.SEARCHING_UIDS,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.CHECKING_POTENTIAL_DUPLICATES -> return StateTransitionFailure("Import już się rozpoczął.")
            ImportStatus.COMPLETE -> return StateTransitionFailure("Import zakończony")
            ImportStatus.IMPORTED,
            ImportStatus.PENDING,
            ImportStatus.COMPLETED_WITH_ERRORS,
            ImportStatus.ERROR -> if (import.savedApplicants == import.totalCount && (import.totalCount ?: 0) > 0) {
                return StateTransitionFailure("Wszyscy są już zapisani")
            }
        }
        return StateTransitionSuccess
    }
}
