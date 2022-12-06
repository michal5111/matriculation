package pl.poznan.ue.matriculation.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository


@Configuration
@Order(1)
class ApiSecurityConfiguration {

    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeHttpRequests {
                authorize("/api/user", permitAll)
                authorize(HttpMethod.POST, "/api/user", hasRole("ADMIN"))
                authorize(HttpMethod.DELETE, "/api/user", hasRole("ADMIN"))
                authorize(HttpMethod.PUT, "/api/user", hasRole("ADMIN"))
                authorize(HttpMethod.GET, "/api/users", hasRole("ADMIN"))
                authorize(HttpMethod.GET, "/api/import/*/progress", hasAnyRole("IMPORT_PROGRESS", "ADMIN"))
                authorize(HttpMethod.GET, "/api/import/*/save", hasAnyRole("IMPORT_SAVE", "ADMIN"))
                authorize(HttpMethod.GET, "/api/import/*/notifications", hasAnyRole("IMPORT_NOTIFICATIONS", "ADMIN"))
                authorize(HttpMethod.GET, "/api/import/dataSources", hasAnyRole("IMPORT_DATA_SOURCES", "ADMIN"))
                authorize(HttpMethod.PUT, "/api/import/*/archive", hasAnyRole("IMPORT_ARCHIVE", "ADMIN"))
                authorize(HttpMethod.PUT, "/api/usos/person/*/indexNumber", hasAnyRole("IMPORT_CHANGE_INDEX", "ADMIN"))
                authorize(HttpMethod.GET, "/api/usos/**", hasAnyRole("USOS_DICTIONARIES", "ADMIN"))
                authorize(HttpMethod.GET, "/api/import/*/applications", hasAnyRole("IMPORT_VIEW_APPLICATIONS", "ADMIN"))
                authorize(HttpMethod.PUT, "/api/import", hasAnyRole("IMPORT_IMPORT_APPLICATIONS", "ADMIN"))
                authorize(HttpMethod.GET, "/api/import", hasAnyRole("IMPORT_VIEW", "ADMIN"))
                authorize(HttpMethod.GET, "/api/import/*", hasAnyRole("IMPORT_VIEW", "ADMIN"))
                authorize(HttpMethod.POST, "/api/import", hasAnyRole("IMPORT_CREATE", "ADMIN"))
                authorize(HttpMethod.DELETE, "/api/import/*", hasAnyRole("IMPORT_DELETE", "ADMIN"))
                authorize(HttpMethod.GET, "/api/import/*/ldap", hasAnyRole("IMPORT_LDAP", "ADMIN"))
                authorize("/actuator/**", permitAll)
            }
            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }
            securityMatcher("/api/**")
            csrf {
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
            }
        }
        return http.build()
    }
}
