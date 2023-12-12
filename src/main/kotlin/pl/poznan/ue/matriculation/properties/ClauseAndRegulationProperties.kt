package pl.poznan.ue.matriculation.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("pl.poznan.ue.matriculation.clause.and.regulation")
@ConstructorBinding
data class ClauseAndRegulationProperties(
    val code: String,
    val programmePattern: String
)
