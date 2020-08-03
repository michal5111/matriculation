package pl.poznan.ue.matriculation.dreamApply.dto.application

data class CorrespondenceDto(
        val municipality: String?,
        val street: String?,
        val region: String?,
        val postalcode: String?,
        val house: String?,
        val country: String?,
        val city: String?,
        val apartment: String?
)
