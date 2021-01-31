package pl.poznan.ue.matriculation.local.dto

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import java.io.Serializable

data class ImportProgressDto(

    val id: Long,

    val importedApplications: Int,

    val saveErrors: Int,

    val savedApplicants: Int,

    val totalCount: Int?,

    val importedUids: Int,

    val importStatus: ImportStatus = ImportStatus.PENDING,

    val error: String? = null
) : Serializable {
    override fun toString(): String {
        return "ImportProgress(id=$id, " +
                "importedApplications=$importedApplications, " +
                "saveErrors=$saveErrors, savedApplicants=$savedApplicants, " +
                "totalCount=$totalCount, importStatus=$importStatus, error=$error)"
    }
}

