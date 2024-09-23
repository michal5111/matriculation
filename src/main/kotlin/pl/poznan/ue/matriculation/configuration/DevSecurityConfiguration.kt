package pl.poznan.ue.matriculation.configuration

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint


@Configuration
@EnableWebSecurity
@Order(2)
@Profile("dev", "dev-frontend-build")
class DevSecurityConfiguration {

    @Value("\${pl.poznan.ue.matriculation.service.home}")
    lateinit var serviceHome: String

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun inMemoryUserDetailsManager(
        auth: AuthenticationManagerBuilder,
        passwordEncoder: PasswordEncoder
    ): UserDetailsManager {
        val user = User.builder()
            .username("admin")
            .passwordEncoder(passwordEncoder::encode)
            .password("admin")
            .roles("ADMIN")
            .build()
        val inMemoryUserDetailsManager = InMemoryUserDetailsManager()
        inMemoryUserDetailsManager.createUser(user)
        return inMemoryUserDetailsManager
    }

    @Bean
    fun devFilterChain(
        http: HttpSecurity,
    ): SecurityFilterChain {
        http {
            securityMatcher("/login*", "/logout")
            csrf { disable() }
            cors { disable() }
            formLogin {
                authenticationSuccessHandler = object : AuthenticationSuccessHandler {
                    override fun onAuthenticationSuccess(
                        request: HttpServletRequest,
                        response: HttpServletResponse,
                        chain: FilterChain,
                        authentication: Authentication?,
                    ) {
                        return response.sendRedirect(serviceHome)
                    }

                    override fun onAuthenticationSuccess(
                        request: HttpServletRequest,
                        response: HttpServletResponse,
                        authentication: Authentication?,
                    ) {
                        return response.sendRedirect(serviceHome)
                    }
                }
            }
            logout {
                logoutUrl = "/logout"
                invalidateHttpSession = true
                deleteCookies("JSESSIONID")
                logoutSuccessHandler =
                    LogoutSuccessHandler { _, response, _ -> response.sendRedirect(serviceHome) }
            }
            authorizeHttpRequests {
                authorize(HttpMethod.GET, "/files/**", hasAnyRole("ADMIN", "SUPER_ADMIN"))
                authorize("/error", permitAll)
                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        val entryPoint = BasicAuthenticationEntryPoint()
        entryPoint.realmName = "test realm"
        return entryPoint
    }
}
