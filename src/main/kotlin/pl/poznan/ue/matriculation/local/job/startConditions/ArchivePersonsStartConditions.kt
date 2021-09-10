package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.exception.ImportStartException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus.*
import pl.poznan.ue.matriculation.local.domain.import.Import

class ArchivePersonsStartConditions : IStartConditions {
    override fun canStart(import: Import) {
        when (import.importProgress.importStatus) {
            PENDING,
            STARTED,
            IMPORTED,
            SAVING,
            ARCHIVED,
            COMPLETED_WITH_ERRORS,
            ERROR,
            SEARCHING_UIDS,
            CHECKING_POTENTIAL_DUPLICATES,
            SENDING_NOTIFICATIONS -> throw ImportStartException(import.id, "Zły stan importu.")
            COMPLETE -> return
        }
    }
}