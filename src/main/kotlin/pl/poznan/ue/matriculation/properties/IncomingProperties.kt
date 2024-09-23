package pl.poznan.ue.matriculation.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pl.poznan.ue.matriculation.incoming")
@ConditionalOnProperty(
    value = ["pl.poznan.ue.matriculation.incoming.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
data class IncomingProperties(
    val instanceKey: String,
    val instanceUrl: String,
    val status: String,
)
