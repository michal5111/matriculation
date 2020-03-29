package pl.ue.poznan.matriculation.local.domain.import

data class ImportDto(
        val programmeCode: String,
        val registration: String,
        val indexPoolCode: String,
        val startDate: String,
        val dateOfAddmision: String,
        val stageCode: String,
        val didacticCycleCode: String
)