package pl.poznan.ue.matriculation.security


import org.slf4j.LoggerFactory
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import pl.poznan.ue.matriculation.local.service.UserService
import java.util.*

internal class CustomUserDetailsService(
    private val admins: Set<String>,
    private val userService: UserService
) : AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    private val log = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    @Throws(UsernameNotFoundException::class)
    override fun loadUserDetails(token: CasAssertionAuthenticationToken): UserDetails {
        val login = token.principal.toString()
        val lowercaseLogin = login.lowercase(Locale.getDefault())
        log.debug("Authenticating '{}'", login)
        val grantedAuthorities = ArrayList<GrantedAuthority>()
        val user = userService.getByUsosId(lowercaseLogin.toLong())
        grantedAuthorities.add(SimpleGrantedAuthority("ROLE_USER"))
        if (admins.contains(lowercaseLogin)) {
            grantedAuthorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        }

        user?.roles?.forEach {
            grantedAuthorities.add(SimpleGrantedAuthority(it.role.code))
        }

        return CasUserDetails(
            userId = user?.uid ?: "N/A",
            authorities = grantedAuthorities,
            casAssertion = token.assertion,
            usosId = user?.usosId
        )
    }
}
