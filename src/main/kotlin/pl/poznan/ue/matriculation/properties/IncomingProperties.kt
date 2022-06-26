package pl.poznan.ue.matriculation.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("pl.poznan.ue.matriculation.incoming")
@ConstructorBinding
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
