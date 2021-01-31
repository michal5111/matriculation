package pl.poznan.ue.matriculation.configuration

import org.jasig.cas.client.session.SingleSignOutFilter
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import pl.poznan.ue.matriculation.local.service.UserService
import pl.poznan.ue.matriculation.security.CustomUserDetailsService

@Configuration
@EnableWebSecurity
@Order(2)
class SecurityConfiguration(val userService: UserService) : WebSecurityConfigurerAdapter() {
    @Value("\${cas.service.login}")
    private lateinit var casUrlLogin: String

    @Value("\${cas.service.logout}")
    private lateinit var casUrlLogout: String

    @Value("\${cas.ticket.validate.url}")
    private lateinit var casValidateUrl: String

    @Value("\${app.service.security}")
    private lateinit var casServiceUrl: String

    @Value("\${pl.poznan.ue.matriculation.service.home}")
    private lateinit var appServiceHome: String

    @Value("\${pl.poznan.ue.matriculation.admin.userName}")
    private lateinit var appAdminUsernames: List<String>

    @Bean
    fun adminList(): Set<String> {
        val admins = HashSet<String>()
        admins.addAll(appAdminUsernames)
        return admins
    }

    @Bean
    fun serviceProperties(): ServiceProperties {
        val sp = ServiceProperties()
        sp.service = casServiceUrl
        sp.isSendRenew = false
        return sp
    }

    @Bean
    fun casAuthenticationProvider(): CasAuthenticationProvider {
        val casAuthenticationProvider = CasAuthenticationProvider()
        casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService())
        casAuthenticationProvider.setServiceProperties(serviceProperties())
        casAuthenticationProvider.setTicketValidator(cas30ServiceTicketValidator())
        casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only")
        return casAuthenticationProvider
    }

    @Bean
    fun customUserDetailsService(): AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
        return CustomUserDetailsService(adminList(), userService)
    }

    @Bean
    fun sessionStrategy(): SessionAuthenticationStrategy {
        return SessionFixationProtectionStrategy()
    }

    @Bean
    fun cas30ServiceTicketValidator(): Cas30ServiceTicketValidator {
        return Cas30ServiceTicketValidator(casValidateUrl)
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
        casAuthenticationEntryPoint.loginUrl = casUrlLogin
        casAuthenticationEntryPoint.serviceProperties = serviceProperties()
        return casAuthenticationEntryPoint
    }

    fun singleSignOutFilter(): SingleSignOutFilter {
        return SingleSignOutFilter()
    }

    @Bean
    fun requestCasGlobalLogoutFilter(): LogoutFilter {
        val logoutFilter = LogoutFilter(
            "$casUrlLogout?service=$appServiceHome",
            SecurityContextLogoutHandler()
        )
        logoutFilter.setLogoutRequestMatcher(AntPathRequestMatcher("/logout", "GET"))
        return logoutFilter
    }

    public override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(casAuthenticationProvider())
    }

    override fun configure(http: HttpSecurity) {
        http
            .exceptionHandling()
            .authenticationEntryPoint(casAuthenticationEntryPoint())
            .and()
            .addFilter(casAuthenticationFilter())
            .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter::class.java)
            .addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter::class.java)
            .authorizeRequests()
            .antMatchers("/login").authenticated()
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        http.headers().frameOptions().disable()
    }

    override fun configure(web: WebSecurity) {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/h2-console/**")
            .antMatchers("/test/**")
    }
}