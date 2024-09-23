package pl.poznan.ue.matriculation.configuration

import org.apereo.cas.client.session.SingleSignOutFilter
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator
import org.apereo.cas.client.validation.TicketValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import pl.poznan.ue.matriculation.local.service.UserService
import pl.poznan.ue.matriculation.properties.CasProperties
import pl.poznan.ue.matriculation.security.CustomUserDetailsService


@Configuration
@Profile("prod")
@Order(2)
class CasConfiguration(
    private val casProperties: CasProperties,
) {
    @Value("\${pl.poznan.ue.matriculation.admin.userName}")
    private lateinit var appAdminUsernames: List<String>

    @Bean
    fun adminList(): Set<String> {
        val admins = HashSet<String>()
        admins.addAll(appAdminUsernames)
        return admins
    }

    @Bean
    fun casAuthenticationEntryPoint(): AuthenticationEntryPoint {
        val casAuthenticationEntryPoint = CasAuthenticationEntryPoint()
        casAuthenticationEntryPoint.loginUrl = casProperties.serviceLogin
        casAuthenticationEntryPoint.serviceProperties = serviceProperties()
        return casAuthenticationEntryPoint
    }

    @Bean
    fun serviceProperties(): ServiceProperties {
        val sp = ServiceProperties()
        sp.service = casProperties.serviceUrl
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
        casAuthenticationProvider.setKey(casProperties.key)
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
        return Cas30ServiceTicketValidator(casProperties.ticketValidateUrl)
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun casAuthenticationFilter(
        serviceProperties: ServiceProperties,
        sessionStrategy: SessionAuthenticationStrategy,
        authenticationManager: AuthenticationManager,
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
            casProperties.serviceLogout,
            SecurityContextLogoutHandler()
        )
        logoutFilter.setLogoutRequestMatcher(AntPathRequestMatcher("/logout", "GET"))
        return logoutFilter
    }

    @Bean
    fun prodFilterChain(
        http: HttpSecurity,
        entryPoint: AuthenticationEntryPoint,
    ): SecurityFilterChain {
        http {
            securityMatcher("/login*", "/logout")
            authorizeHttpRequests {
                authorize("/error", permitAll)
                authorize(anyRequest, authenticated)
            }
            addFilterBefore<CasAuthenticationFilter>(singleSignOutFilter())
            addFilterBefore<LogoutFilter>(requestCasGlobalLogoutFilter())
            exceptionHandling {
                authenticationEntryPoint = entryPoint
            }
        }
        return http.build()
    }
}
