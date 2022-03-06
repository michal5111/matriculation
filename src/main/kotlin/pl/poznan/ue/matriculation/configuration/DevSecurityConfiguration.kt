package pl.poznan.ue.matriculation.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint


@Configuration
@EnableWebSecurity
@Order(2)
@Profile("dev", "dev-frontend-build")
class DevSecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
            .withUser("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN")
    }

    override fun configure(web: WebSecurity) {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/h2-console/**")
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .antMatcher("/login*")
            .authorizeRequests().anyRequest().authenticated()
            .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint())
            .and().logout()
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint? {
        val entryPoint = BasicAuthenticationEntryPoint()
        entryPoint.realmName = "test realm"
        return entryPoint
    }
}
