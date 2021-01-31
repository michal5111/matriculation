package pl.poznan.ue.matriculation.ldap.repo

import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.ldap.model.User

@Repository
interface LdapUserRepository : LdapRepository<User> {
    fun findByUsosId(usosId: Long): User?

    fun findByUid(uid: String): User?
}