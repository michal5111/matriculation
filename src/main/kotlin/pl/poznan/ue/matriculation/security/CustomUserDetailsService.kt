package pl.poznan.ue.matriculation.security

import org.slf4j.LoggerFactory
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

internal class CustomUserDetailsService(
        private val admins: Set<String>?,
        private val users: Set<String>?
) : AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    private val log = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    @Throws(UsernameNotFoundException::class)
    override fun loadUserDetails(token: CasAssertionAuthenticationToken): UserDetails {
        val login = token.principal.toString()
        val lowercaseLogin = login.toLowerCase()

        log.debug("Authenticating '{}'", login)
        val grantedAuthorities = ArrayList<GrantedAuthority>()

        if (admins != null && admins.contains(lowercaseLogin)) {
            grantedAuthorities.add(SimpleGrantedAuthority(ADMIN))
        } else if (users != null && users.contains(lowercaseLogin)) {
            grantedAuthorities.add(SimpleGrantedAuthority(USER))
        } else {
            grantedAuthorities.add(SimpleGrantedAuthority(ANONYMOUS))
        }

        return CasUserDetails(lowercaseLogin, grantedAuthorities, token.assertion)
    }

    companion object {

        const val ADMIN = "ROLE_ADMIN"

        const val USER = "ROLE_USER"

        const val ANONYMOUS = "ROLE_ANONYMOUS"
    }
}