package pl.poznan.ue.matriculation.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pl.poznan.ue.matriculation.clause.and.regulation2")
data class ClauseAndRegulationPropertiesList(
    val regulations: List<ClauseAndRegulationProperties>
)
