package pl.poznan.ue.matriculation.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import pl.poznan.ue.matriculation.cem.enum.ApplicationStatus

@ConfigurationProperties("pl.poznan.ue.matriculation.cem")
@ConstructorBinding
@ConditionalOnProperty(
    value = ["pl.poznan.ue.matriculation.cem.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
data class CemDatasourceProperties(
    val instanceUrl: String,

    val applicationStatus: ApplicationStatus,

    val db: CemDBProperties
)
