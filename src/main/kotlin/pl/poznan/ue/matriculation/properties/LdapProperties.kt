package pl.poznan.ue.matriculation.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("pl.poznan.ue.matriculation.ldap")
@ConstructorBinding
data class LdapProperties(
    val url: String,
    val base: String,
    val dn: String,
    val password: String
)
