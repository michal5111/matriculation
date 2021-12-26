package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.ImportStartException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class CheckForPotentialDuplicatesStartConditions : IStartConditions {
    override fun canStart(import: Import) {
        when (import.importStatus) {
            ImportStatus.ARCHIVED -> throw ImportException(import.id, "Import został zarchiwizowany.")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.ERROR,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.COMPLETE,
            ImportStatus.COMPLETED_WITH_ERRORS,
            ImportStatus.SEARCHING_UIDS,
            ImportStatus.CHECKING_POTENTIAL_DUPLICATES -> throw ImportStartException(
                import.id,
                "Zły stan importu. ${import.importStatus}"
            )
            ImportStatus.IMPORTED -> {
                val totalCount = import.totalCount
                    ?: throw ImportStartException(import.id, "Liczba aplikantów to null")
                if (totalCount < 1 || totalCount == import.savedApplicants) {
                    throw ImportStartException(import.id, "Liczba aplikantów wynosi 0 lub wszyscy są już zapisani")
                }
            }
        }
    }
}
