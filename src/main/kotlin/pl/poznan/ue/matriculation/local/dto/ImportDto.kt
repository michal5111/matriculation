package pl.poznan.ue.matriculation.local.dto

import java.util.*

class ImportDto(
        val programmeCode: String,
        val programmeForeignId: String,
        val registration: String,
        val indexPoolCode: String,
        val startDate: Date,
        val dateOfAddmision: Date,
        val stageCode: String,
        val didacticCycleCode: String,
        val dataSourceId: String
)