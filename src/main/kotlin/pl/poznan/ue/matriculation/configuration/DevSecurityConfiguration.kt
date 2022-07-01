package pl.poznan.ue.matriculation.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint


@Configuration
@EnableWebSecurity
@Order(2)
@Profile("dev", "dev-frontend-build")
class DevSecurityConfiguration {

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
        authenticationEntryPoint: AuthenticationEntryPoint
    ): SecurityFilterChain {
        http
            .csrf().disable()
            .antMatcher("/login*")
            .authorizeRequests().anyRequest().authenticated()
            .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint)
            .and().logout()
        return http.build()
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        val entryPoint = BasicAuthenticationEntryPoint()
        entryPoint.realmName = "test realm"
        return entryPoint
    }
}
