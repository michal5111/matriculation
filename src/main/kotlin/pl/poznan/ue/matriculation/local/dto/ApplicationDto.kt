package pl.poznan.ue.matriculation.local.dto

import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import java.io.Serializable

/**
 * A DTO for the {@link pl.poznan.ue.matriculation.local.domain.applications.Application} entity
 */
data class ApplicationDto(
    val id: Long? = null,

    val foreignId: Long? = null,

    val dataSourceId: String? = null,

    val editUrl: String? = null,

    val certificate: DocumentDto? = null,

    val importStatus: ApplicationImportStatus = ApplicationImportStatus.NOT_IMPORTED,

    val importError: String? = null,

    val stackTrace: String? = null,

    val applicant: ApplicantDto? = null,

    val baseOfStay: String? = null,

    val basisOfAdmission: String? = null,

    val sourceOfFinancing: String? = null,

    val notificationSent: Boolean = false,

    val warnings: String? = null,
    val importId: Long?
) : Serializable
