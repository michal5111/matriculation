package pl.poznan.ue.matriculation.local.dto

import jakarta.validation.constraints.NotNull
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import java.time.LocalDate

data class ImportDto(
    val id: Long? = null,
    @NotNull
    val programmeCode: String? = null,
    @NotNull
    val programmeForeignId: String? = null,
    @NotNull
    val programmeForeignName: String? = null,
    @NotNull
    val registration: String? = null,

    val indexPoolCode: String? = null,

    val indexPoolName: String? = null,
    @NotNull
    val startDate: LocalDate? = null,
    @NotNull
    val dateOfAddmision: LocalDate? = null,
    @NotNull
    val stageCode: String? = null,
    @NotNull
    val didacticCycleCode: String? = null,
    @NotNull
    val dataSourceId: String? = null,
    @NotNull
    val dataSourceName: String? = null,
    val additionalProperties: Map<String, Any>? = null,

    val importedApplications: Int? = null,

    val saveErrors: Int? = null,

    val savedApplicants: Int? = null,

    val totalCount: Int? = null,

    val importedUids: Int? = null,

    val notificationsSend: Int? = null,

    val potentialDuplicates: Int? = null,

    val importStatus: ImportStatus? = null,

    val error: String? = null,

    val stackTrace: String? = null,
)
