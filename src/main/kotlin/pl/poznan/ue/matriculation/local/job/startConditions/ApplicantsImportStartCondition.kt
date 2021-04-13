package pl.poznan.ue.matriculation.local.job.startConditions

import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

class ApplicantsImportStartCondition : IStartConditions {
    override fun canStart(import: Import) {
        when (import.importProgress.importStatus) {
            ImportStatus.ARCHIVED -> throw ImportException(import.id!!, "Import został zarchiwizowany.")
            ImportStatus.STARTED,
            ImportStatus.SAVING,
            ImportStatus.SEARCHING_UIDS,
            ImportStatus.SENDING_NOTIFICATIONS -> throw ImportException(import.id!!, "Import już się rozpoczął.")
            ImportStatus.COMPLETE -> throw ImportException(import.id!!, "Import zakończony")
            ImportStatus.IMPORTED,
            ImportStatus.PENDING,
            ImportStatus.COMPLETED_WITH_ERRORS,
            ImportStatus.ERROR -> if (import.importProgress.savedApplicants == import.importProgress.totalCount) {
                throw ImportException(import.id!!, "Wszyscy są już zapisani")
            }
        }
    }
}