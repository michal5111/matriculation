package pl.poznan.ue.matriculation.configuration

import org.jasig.cas.client.session.SingleSignOutFilter
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator
import org.jasig.cas.client.validation.TicketValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationFilter
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import pl.poznan.ue.matriculation.local.service.UserService
import pl.poznan.ue.matriculation.security.CustomUserDetailsService

@Configuration
@Profile("prod")
class CasConfiguration {

    @Value("\${cas.service.logout}")
    private lateinit var casUrlLogout: String

    @Value("\${cas.ticket.validate.url}")
    private lateinit var casValidateUrl: String

    @Value("\${app.service.security}")
    private lateinit var casServiceUrl: String

    @Value("\${pl.poznan.ue.matriculation.service.home}")
    private lateinit var appServiceHome: String

    @Value("\${pl.poznan.ue.matriculation.admin.userName}")
    private lateinit var appAdminUsernames: List<String>

    @Bean
    fun adminList(): Set<String> {
        val admins = HashSet<String>()
        admins.addAll(appAdminUsernames)
        return admins
    }

    @Bean
    fun serviceProperties(): ServiceProperties {
        val sp = ServiceProperties()
        sp.service = casServiceUrl
        sp.isSendRenew = false
        return sp
    }

    @Bean
    fun casAuthenticationProvider(
        serviceProperties: ServiceProperties,
        ticketValidator: TicketValidator,
        authenticationUserDetailsService: AuthenticationUserDetailsService<CasAssertionAuthenticationToken>
    ): CasAuthenticationProvider {
        val casAuthenticationProvider = CasAuthenticationProvider()
        casAuthenticationProvider.setAuthenticationUserDetailsService(authenticationUserDetailsService)
        casAuthenticationProvider.setServiceProperties(serviceProperties)
        casAuthenticationProvider.setTicketValidator(ticketValidator)
        casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only")
        return casAuthenticationProvider
    }

    @Bean
    fun customUserDetailsService(
        userService: UserService
    ): AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
        return CustomUserDetailsService(adminList(), userService)
    }

    @Bean
    fun sessionStrategy(): SessionAuthenticationStrategy {
        return SessionFixationProtectionStrategy()
    }

    @Bean
    fun cas30ServiceTicketValidator(): TicketValidator {
        return Cas30ServiceTicketValidator(casValidateUrl)
    }

    @Bean
    fun casAuthenticationFilter(
        serviceProperties: ServiceProperties,
        sessionStrategy: SessionAuthenticationStrategy,
        authenticationManager: AuthenticationManager
    ): CasAuthenticationFilter {
        val casAuthenticationFilter = CasAuthenticationFilter()
        casAuthenticationFilter.setAuthenticationManager(authenticationManager)
        casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy)
        casAuthenticationFilter.setServiceProperties(serviceProperties)
        return casAuthenticationFilter
    }

    @Bean
    fun singleSignOutFilter(): SingleSignOutFilter {
        return SingleSignOutFilter()
    }

    @Bean
    fun requestCasGlobalLogoutFilter(): LogoutFilter {
        val logoutFilter = LogoutFilter(
            "$casUrlLogout?service=$appServiceHome",
            SecurityContextLogoutHandler()
        )
        logoutFilter.setLogoutRequestMatcher(AntPathRequestMatcher("/logout", "GET"))
        return logoutFilter
    }
}
