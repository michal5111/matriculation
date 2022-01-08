package pl.poznan.ue.matriculation.local.dto

import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus

data class ApplicationBasicDto(
    val id: Long?,
    val foreignId: Long,
    val dataSourceId: String?,
    val editUrl: String?,
    val importStatus: ApplicationImportStatus,
    val importError: String?,
    val stackTrace: String?,
    val certificate: DocumentDto?,
    val applicant: ApplicantBasicDto?,
    val importId: Long?,
)
