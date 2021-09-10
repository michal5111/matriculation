package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.ImportStartException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class SavePersonsStartConditions : IStartConditions {
    override fun canStart(import: Import) {
        when (import.importProgress.importStatus) {
            ImportStatus.ARCHIVED -> throw ImportException(import.id, "Import został zarchiwizowany.")
            ImportStatus.PENDING,
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.ERROR,
            ImportStatus.SENDING_NOTIFICATIONS,
            ImportStatus.CHECKING_POTENTIAL_DUPLICATES,
            ImportStatus.SEARCHING_UIDS -> throw ImportStartException(
                import.id,
                "Zły stan importu. ${import.importProgress.importStatus} ${import.importProgress.error.orEmpty()}"
            )
            ImportStatus.IMPORTED,
            ImportStatus.COMPLETE,
            ImportStatus.COMPLETED_WITH_ERRORS -> {
                val totalCount = import.importProgress.totalCount
                    ?: throw ImportStartException(import.id, "Liczba aplikantów to null")
                if (totalCount < 1 || totalCount == import.importProgress.savedApplicants) {
                    throw ImportStartException(import.id, "Liczba aplikantów wynosi 0 lub wszyscy są już zapisani")
                }
            }
        }
    }
}