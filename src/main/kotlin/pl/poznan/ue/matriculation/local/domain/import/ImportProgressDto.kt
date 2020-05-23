package pl.poznan.ue.matriculation.local.domain.import

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import java.io.Serializable

data class ImportProgressDto(

        var id: Long,

        var importedApplications: Int,

        var saveErrors: Int,

        var savedApplicants: Int,

        var totalCount: Int,

        var importStatus: ImportStatus = ImportStatus.PENDING,

        var error: String? = null
): Serializable {
        override fun toString(): String {
                return "ImportProgress(id=$id, " +
                        "importedApplications=$importedApplications, " +
                        "saveErrors=$saveErrors, savedApplicants=$savedApplicants, " +
                        "totalCount=$totalCount, importStatus=$importStatus, error=$error)"
        }
}

