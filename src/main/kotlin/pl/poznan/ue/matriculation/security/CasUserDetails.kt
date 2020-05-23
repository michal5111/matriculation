package pl.poznan.ue.matriculation.security

import org.jasig.cas.client.validation.Assertion
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class CasUserDetails
(private val userid: String, private val authorities: Collection<GrantedAuthority>,
 val casAssertion: Assertion) : UserDetails {

    private val roles = ArrayList<String>()

    init {
        for (authority in authorities) {
            this.roles.add(authority.authority)
        }
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return userid
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}