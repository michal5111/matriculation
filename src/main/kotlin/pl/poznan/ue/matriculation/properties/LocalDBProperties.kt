package pl.poznan.ue.matriculation.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("local.datasource")
@ConstructorBinding
data class LocalDBProperties(
    val driverClassName: String,
    val username: String,
    val password: String,
    val url: String,
    val jpa: Map<String, String>?
)
