package pl.poznan.ue.matriculation.configuration

import org.jasig.cas.client.session.SingleSignOutFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository


@Configuration
@EnableWebSecurity
@Order(2)
@Profile("prod")
class SecurityConfiguration(
    val serviceProperties: ServiceProperties,
    val casAuthenticationProvider: CasAuthenticationProvider,
    val singleSignOutFilter: SingleSignOutFilter,
    val logoutFilter: LogoutFilter
) {

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return ProviderManager(listOf(casAuthenticationProvider))
    }

    @Value("\${cas.service.login}")
    private lateinit var casUrlLogin: String

    @Bean
    fun casAuthenticationEntryPoint(): AuthenticationEntryPoint {
        val casAuthenticationEntryPoint = CasAuthenticationEntryPoint()
        casAuthenticationEntryPoint.loginUrl = casUrlLogin
        casAuthenticationEntryPoint.serviceProperties = serviceProperties
        return casAuthenticationEntryPoint
    }

    @Bean
    fun loginFilterChain(
        http: HttpSecurity,
        entryPoint: AuthenticationEntryPoint
    ): SecurityFilterChain {
        http.invoke {
            httpBasic { disable() }
            authorizeHttpRequests {
                authorize("/login", authenticated)
            }
            addFilterBefore<CasAuthenticationFilter>(singleSignOutFilter)
            addFilterBefore<LogoutFilter>(logoutFilter)
            exceptionHandling {
                authenticationEntryPoint = entryPoint
            }
            csrf {
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
            }
            headers {
                frameOptions { disable() }
            }
        }
        return http.build()
    }
}
