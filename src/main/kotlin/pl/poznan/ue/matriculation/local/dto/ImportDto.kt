package pl.poznan.ue.matriculation.local.dto

import java.util.*

class ImportDto(
    val programmeCode: String,
    val programmeForeignId: String,
    val programmeForeignName: String,
    val registration: String,
    val indexPoolCode: String,
    val indexPoolName: String,
    val startDate: Date,
    val dateOfAddmision: Date,
    val stageCode: String,
    val didacticCycleCode: String,
    val dataSourceId: String,
    val additionalProperties: Map<String, Any>?
)
