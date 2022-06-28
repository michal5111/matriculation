package pl.poznan.ue.matriculation.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository


@Configuration
@Order(1)
class ApiSecurityConfiguration {

    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests()
            .antMatchers("/api/user").permitAll()
            .antMatchers(HttpMethod.POST, "/api/user").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/api/user").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "/api/user").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api/import/*/progress").hasAnyRole("IMPORT_PROGRESS", "ADMIN")
            .antMatchers(HttpMethod.GET, "/api/import/*/save").hasAnyRole("IMPORT_SAVE", "ADMIN")
            .antMatchers(HttpMethod.GET, "/api/import/*/notifications").hasAnyRole("IMPORT_NOTIFICATIONS", "ADMIN")
            .antMatchers(HttpMethod.GET, "/api/import/dataSources").hasAnyRole("IMPORT_DATA_SOURCES", "ADMIN")
            .antMatchers(HttpMethod.PUT, "/api/import/*/archive").hasAnyRole("IMPORT_ARCHIVE", "ADMIN")
            .antMatchers(HttpMethod.PUT, "/api/usos/person/*/indexNumber")
            .hasAnyRole("IMPORT_CHANGE_INDEX", "ADMIN")
            .antMatchers(HttpMethod.GET, "/api/usos/**").hasAnyRole("USOS_DICTIONARIES", "ADMIN")
            .antMatchers(HttpMethod.GET, "/api/import/*/applications")
            .hasAnyRole("IMPORT_VIEW_APPLICATIONS", "ADMIN")
            .antMatchers(HttpMethod.PUT, "/api/import").hasAnyRole("IMPORT_IMPORT_APPLICATIONS", "ADMIN")
            .antMatchers(HttpMethod.GET, "/api/import").hasAnyRole("IMPORT_VIEW", "ADMIN")
            .antMatchers(HttpMethod.GET, "/api/import/*").hasAnyRole("IMPORT_VIEW", "ADMIN")
            .antMatchers(HttpMethod.POST, "/api/import").hasAnyRole("IMPORT_CREATE", "ADMIN")
            .antMatchers(HttpMethod.DELETE, "/api/import/*").hasAnyRole("IMPORT_DELETE", "ADMIN")
            .antMatchers(HttpMethod.GET, "/api/import/*/ldap").hasAnyRole("IMPORT_LDAP", "ADMIN")
            .antMatchers("/actuator/**").permitAll()
            .and()
            .antMatcher("/api/**")
            .exceptionHandling()
            .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        return http.build()
    }
}
