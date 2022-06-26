package pl.poznan.ue.matriculation.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("pl.poznan.ue.matriculation.dream-apply")
@ConstructorBinding
@ConditionalOnProperty(
    value = ["pl.poznan.ue.matriculation.dreamApply.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
data class DreamApplyProperties(
    val instanceKey: String,
    val instanceUrl: String,
    val status: String,
)
