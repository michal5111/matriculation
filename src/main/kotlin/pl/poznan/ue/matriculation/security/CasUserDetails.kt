package pl.poznan.ue.matriculation.security

import org.apereo.cas.client.validation.Assertion
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CasUserDetails(
    private val userId: String,
    private val authorities: Collection<GrantedAuthority>,
    val casAssertion: Assertion,
    val usosId: Long?
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return userId
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

    override fun toString(): String {
        return "CasUserDetails(userId='$userId', authorities=$authorities, usosId=$usosId)"
    }
}
