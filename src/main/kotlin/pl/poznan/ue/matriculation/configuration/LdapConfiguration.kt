package pl.poznan.ue.matriculation.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.ldap.repository.config.EnableLdapRepositories
import org.springframework.ldap.core.ContextSource
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import pl.poznan.ue.matriculation.properties.LdapProperties
import javax.naming.Context

@Configuration
@EnableLdapRepositories
class LdapConfiguration(
    private val ldapProperties: LdapProperties
) {

    @Bean
    fun contextSource(): ContextSource {
        val context = LdapContextSource()
        val baseEnvironmentProperties: MutableMap<String, Any> = HashMap()
        baseEnvironmentProperties[Context.SECURITY_AUTHENTICATION] = "simple"
        context.setUrl(ldapProperties.url)
        context.setBase(ldapProperties.base)
        context.userDn = ldapProperties.dn
        context.password = ldapProperties.password
        context.setBaseEnvironmentProperties(baseEnvironmentProperties)
        context.afterPropertiesSet()
        return context
    }

    @Bean
    fun ldapTemplate(contextSource: ContextSource): LdapTemplate {
        val ldapTemplate = LdapTemplate()
        ldapTemplate.contextSource = contextSource
        ldapTemplate.setIgnorePartialResultException(true)
        return ldapTemplate
    }
}
