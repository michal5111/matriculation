package pl.ue.poznan.matriculation.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import pl.ue.poznan.matriculation.security.APIKeyAuthFilter


@Configuration
@Order(1)
class ApiSecurityConfiguration : WebSecurityConfigurerAdapter() {

    private val principalRequestHeader: String = "key"

    private val principalRequestValue: String = "test"

    override fun configure(http: HttpSecurity) {
        val filter = APIKeyAuthFilter(principalRequestHeader)
        filter.setAuthenticationManager { authentication ->
            println(principalRequestHeader)
            println(principalRequestValue)
            println(authentication.principal)
            if (principalRequestValue != authentication.principal) {
                throw BadCredentialsException("The API key was not found or not the expected value.")
            }
            authentication.isAuthenticated = true
            authentication
        }

//        http.authorizeRequests()
//                .antMatchers("/api/user").permitAll()
//                .and()
//                .antMatcher("/api/**")
//                    .exceptionHandling()
//                    .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                .and()
//                .addFilter(filter).authorizeRequests().anyRequest().authenticated()
        http.antMatcher("/api/**")
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(filter)
                .addFilterBefore(ExceptionTranslationFilter(
                        Http403ForbiddenEntryPoint()),
                        filter.javaClass
                )
                .authorizeRequests()
                .anyRequest()
                .authenticated()
    }
}