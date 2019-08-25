package pl.ue.poznan.matriculation.configuration

import org.jasig.cas.client.session.SingleSignOutFilter
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import pl.ue.poznan.matriculation.security.CustomUserDetailsService
import java.util.*

@Configuration
@EnableWebSecurity
@Order(2)
class SecurityConfiguration: WebSecurityConfigurerAdapter() {
    @Value("\${cas.service.login}")
    private lateinit var CAS_URL_LOGIN: String
    @Value("\${cas.service.logout}")
    private lateinit var CAS_URL_LOGOUT: String
    @Value("\${cas.url.prefix}")
    private lateinit var CAS_URL_PREFIX: String
    @Value("\${cas.ticket.validate.url}")
    private lateinit var CAS_VALIDATE_URL: String
    @Value("\${app.service.security}")
    private lateinit var CAS_SERVICE_URL: String
    @Value("\${app.service.home}")
    private lateinit var APP_SERVICE_HOME: String
    @Value("\${app.admin.userName:admin}")
    private lateinit var APP_ADMIN_USER_NAME: String

    @Bean
    fun adminList(): Set<String> {
        val admins = HashSet<String>()
        admins.add(APP_ADMIN_USER_NAME)
        return admins
    }

    @Bean
    fun serviceProperties(): ServiceProperties {
        val sp = ServiceProperties()
        sp.service = CAS_SERVICE_URL
        sp.isSendRenew = false
        return sp
    }

    @Bean
    fun casAuthenticationProvider(): CasAuthenticationProvider {
        val casAuthenticationProvider = CasAuthenticationProvider()
        casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService())
        casAuthenticationProvider.setServiceProperties(serviceProperties())
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator())
        casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only")
        return casAuthenticationProvider
    }

    @Bean
    fun customUserDetailsService(): AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
        return CustomUserDetailsService(adminList())
    }

    @Bean
    fun sessionStrategy(): SessionAuthenticationStrategy {
        return SessionFixationProtectionStrategy()
    }

    @Bean
    fun cas20ServiceTicketValidator(): Cas20ServiceTicketValidator {
        return Cas20ServiceTicketValidator(CAS_VALIDATE_URL)
    }

    @Bean
    @Throws(Exception::class)
    fun casAuthenticationFilter(): CasAuthenticationFilter {
        val casAuthenticationFilter = CasAuthenticationFilter()
        casAuthenticationFilter.setAuthenticationManager(authenticationManager())
        casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy())
        return casAuthenticationFilter
    }

    fun casAuthenticationEntryPoint(): CasAuthenticationEntryPoint {
        val casAuthenticationEntryPoint = CasAuthenticationEntryPoint()
        casAuthenticationEntryPoint.loginUrl = CAS_URL_LOGIN
        casAuthenticationEntryPoint.serviceProperties = serviceProperties()
        return casAuthenticationEntryPoint
    }

    fun singleSignOutFilter(): SingleSignOutFilter {
        val singleSignOutFilter = SingleSignOutFilter()
        singleSignOutFilter.setCasServerUrlPrefix(CAS_URL_PREFIX)
        return singleSignOutFilter
    }

    @Bean
    fun requestCasGlobalLogoutFilter(): LogoutFilter {
        val logoutFilter = LogoutFilter(
                "$CAS_URL_LOGOUT?service=$APP_SERVICE_HOME",
                SecurityContextLogoutHandler())
        logoutFilter.setLogoutRequestMatcher(AntPathRequestMatcher("/logout", "GET"))
        return logoutFilter
    }

    public override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(casAuthenticationProvider())
    }

//    @Throws(Exception::class)
//    override fun configure(web: WebSecurity) {
//        web.ignoring().antMatchers("/fonts/**").antMatchers("/images/**").antMatchers("/scripts/**").antMatchers("/styles/**")
//                .antMatchers("/views/**").antMatchers("/i18n/**").antMatchers("/webjars/**")
//    }

    override fun configure(http: HttpSecurity) {
        http
                .antMatcher("/**")
                .exceptionHandling()
                    .authenticationEntryPoint(casAuthenticationEntryPoint())
                .and()
                    .addFilter(casAuthenticationFilter())
                    .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter::class.java)
                    .addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter::class.java)
                    .authorizeRequests().anyRequest().authenticated()
    }
}