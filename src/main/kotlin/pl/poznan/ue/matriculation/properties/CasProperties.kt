package pl.poznan.ue.matriculation.properties

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile

@Profile("prod")
@ConfigurationProperties("pl.poznan.ue.matriculation.cas")
data class CasProperties(
    @NotBlank
    val serviceUrl: String,
    @NotBlank
    val url: String,
    @NotBlank
    val serviceLogin: String,
    @NotBlank
    val serviceLogout: String,
    @NotBlank
    val ticketValidateUrl: String,
    @NotBlank
    val key: String,
)
