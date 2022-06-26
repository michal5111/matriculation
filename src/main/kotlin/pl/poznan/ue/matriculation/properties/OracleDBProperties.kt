package pl.poznan.ue.matriculation.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("oracle.datasource")
@ConstructorBinding
data class OracleDBProperties(
    val driverClassName: String,
    val jpa: Map<String, String>?,
    val username: String,
    val password: String,
    val url: String
)
