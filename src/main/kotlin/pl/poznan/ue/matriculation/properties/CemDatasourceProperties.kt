package pl.poznan.ue.matriculation.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import pl.poznan.ue.matriculation.cem.enum.ApplicationStatus

@ConfigurationProperties("pl.poznan.ue.matriculation.cem")
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
