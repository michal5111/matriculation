package pl.ue.poznan.matriculation.security

import org.slf4j.LoggerFactory
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

internal class CustomUserDetailsService
/**
 * @param admins
 */
(private val admins: Set<String>?) : AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    private val log = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    @Throws(UsernameNotFoundException::class)
    override fun loadUserDetails(token: CasAssertionAuthenticationToken): UserDetails {
        val login = token.principal.toString()
        val lowercaseLogin = login.toLowerCase()

        log.debug("Authenticating '{}'", login)
        val grantedAuthorities = ArrayList<GrantedAuthority>()

        if (admins != null && admins.contains(lowercaseLogin)) {
            grantedAuthorities.add(SimpleGrantedAuthority(ADMIN))
        } else {
            grantedAuthorities.add(object : GrantedAuthority {
                private val serialVersionUID = 1L

                override fun getAuthority(): String {
                    return USER
                }
            })
        }

        return CasUserDetails(lowercaseLogin, grantedAuthorities, token.assertion)
    }

    companion object {

        val ADMIN = "ROLE_ADMIN"

        val USER = "ROLE_USER"

        val ANONYMOUS = "ROLE_ANONYMOUS"
    }
}