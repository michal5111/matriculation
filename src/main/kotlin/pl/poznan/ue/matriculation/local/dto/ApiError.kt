package pl.poznan.ue.matriculation.local.dto

data class ApiError(
        val status: Int,
        val message: String?,
        val localizedMessage: String? = null
) {

}