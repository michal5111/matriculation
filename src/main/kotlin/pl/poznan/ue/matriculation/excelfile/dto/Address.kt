package pl.poznan.ue.matriculation.excelfile.dto

data class Address(
    var countryCode: String?,

    var flatNumber: String?,

    var postalCode: String?,

    var street: String?,

    var streetNumber: String?,

    val city: String?
)