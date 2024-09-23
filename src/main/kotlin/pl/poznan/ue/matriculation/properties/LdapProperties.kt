package pl.poznan.ue.matriculation.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pl.poznan.ue.matriculation.ldap")
data class LdapProperties(
    val url: String,
    val base: String,
    val dn: String,
    val password: String
)
