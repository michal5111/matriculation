package pl.ue.poznan.matriculation.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@Configuration
@Order(1)
class ApiSecurityConfiguration: WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests().antMatchers("/api/user").permitAll()
                .and()
                .antMatcher("/api/**")
                    .exceptionHandling()
                        .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .and()
                    .authorizeRequests().anyRequest().authenticated()

    }
}