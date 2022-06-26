package pl.poznan.ue.matriculation.properties

data class CemDBProperties(
    val driverClassName: String,
    val jpa: Map<String, String>?,
    val username: String,
    val password: String,
    val url: String
)
