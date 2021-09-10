package pl.poznan.ue.matriculation.dreamApply.dto.application

data class AddressDto(
    val street: String?,
    val postalcode: String?,
    val country: String?,
    val house: String?,
    val apartment: String?,
    val correspondence: CorrespondenceDto?,
    val city: String?,
    val region: String?,
    val postoffice: String?
)
