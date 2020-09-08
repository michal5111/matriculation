package pl.poznan.ue.matriculation.configuration

import org.jasig.cas.client.session.SingleSignOutFilter
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
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

//    @Value("\${pl.poznan.ue.matriculation.cas.url}")
//    private lateinit var CAS_URL_PREFIX: String

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
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator())
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
    fun cas20ServiceTicketValidator(): Cas20ServiceTicketValidator {
        return Cas20ServiceTicketValidator(casValidateUrl)
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
        //singleSignOutFilter.(CAS_URL_PREFIX)//setCasServerUrlPrefix(CAS_URL_PREFIX)
        return SingleSignOutFilter()
    }

    @Bean
    fun requestCasGlobalLogoutFilter(): LogoutFilter {
        val logoutFilter = LogoutFilter(
                "$casUrlLogout?service=$appServiceHome",
                SecurityContextLogoutHandler())
        logoutFilter.setLogoutRequestMatcher(AntPathRequestMatcher("/logout", "GET"))
        return logoutFilter
    }

    public override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(casAuthenticationProvider())
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("OPTIONS")
        config.addAllowedMethod("GET")
        config.addAllowedMethod("POST")
        config.addAllowedMethod("PUT")
        config.addAllowedMethod("DELETE")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    override fun configure(http: HttpSecurity) {
        http
                .exceptionHandling()
                .authenticationEntryPoint(casAuthenticationEntryPoint())
                .and()
                .addFilter(casAuthenticationFilter())
                .addFilterBefore(corsFilter(), CorsFilter::class.java)
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter::class.java)
                .addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter::class.java)
                .authorizeRequests()
                .antMatchers("/login").authenticated()
        http.csrf().disable()
        http.headers().frameOptions().disable()
    }

    override fun configure(web: WebSecurity) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/i18n/**")
                .antMatchers("/content/**")
                .antMatchers("/h2-console/**")
                .antMatchers("/swagger-ui/index.html")
                .antMatchers("/test/**")
    }
}