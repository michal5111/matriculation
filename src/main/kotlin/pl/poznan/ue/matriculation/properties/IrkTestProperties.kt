package pl.poznan.ue.matriculation.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("pl.poznan.ue.matriculation.irk.test")
@ConstructorBinding
@ConditionalOnProperty(
    value = ["pl.poznan.ue.matriculation.irk.test.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
data class IrkTestProperties(
    val instanceUrl: String,
    val instanceKey: String,
    val setAsAccepted: Boolean
)
