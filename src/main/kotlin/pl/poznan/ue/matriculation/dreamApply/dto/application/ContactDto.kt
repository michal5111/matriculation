package pl.poznan.ue.matriculation.dreamApply.dto.application

data class ContactDto(
        val address: AddressDto?,
        val telephone: TelephoneDto?,
        val email: String?,
        val emergency: EmergencyDto?
) {

}
