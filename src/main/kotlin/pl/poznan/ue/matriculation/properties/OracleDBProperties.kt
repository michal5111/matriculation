package pl.poznan.ue.matriculation.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.bind.Name

@ConfigurationProperties("oracle.datasource")
@ConstructorBinding
data class OracleDBProperties(
    val driverClassName: String,
    @Name("database-platform") val databasePlatform: String,
    val username: String,
    val password: String,
    val url: String
)
