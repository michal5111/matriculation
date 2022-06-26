package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus.*
import pl.poznan.ue.matriculation.local.domain.import.Import

class ArchivePersonsStartConditions : IStartConditions {
    override fun canStart(import: Import): StateTransitionResult {
        when (import.importStatus) {
            PENDING,
            STARTED,
            IMPORTED,
            SAVING,
            ARCHIVED,
            COMPLETED_WITH_ERRORS,
            ERROR,
            SEARCHING_UIDS,
            CHECKING_POTENTIAL_DUPLICATES,
            SENDING_NOTIFICATIONS -> return StateTransitionFailure("Zły stan importu.")
            COMPLETE -> return StateTransitionSuccess
        }
    }
}
