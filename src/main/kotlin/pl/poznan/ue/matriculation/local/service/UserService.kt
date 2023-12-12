package pl.poznan.ue.matriculation.local.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.UidNotFoundException
import pl.poznan.ue.matriculation.exception.UserNotFoundException
import pl.poznan.ue.matriculation.ldap.repo.LdapUserRepository
import pl.poznan.ue.matriculation.local.domain.user.User
import pl.poznan.ue.matriculation.local.repo.UserRepository
import javax.persistence.EntityNotFoundException

@Service
@Transactional(rollbackFor = [Exception::class])
class UserService(
    val userRepository: UserRepository,
    val ldapUserRepository: LdapUserRepository
) {

    companion object {
        fun checkDataSourcePermission(dataSourceId: String): Boolean {
            val userDetails = SecurityContextHolder.getContext().authentication
            val permissions = userDetails.authorities.map { it.authority }
            val dataSourcePermissions = permissions
                .filter { it.startsWith("ROLE_DATASOURCE_OPERATOR_") }
                .map { it.substringAfter("ROLE_DATASOURCE_OPERATOR_") }
            return permissions.contains("ROLE_ADMIN") || dataSourcePermissions.contains(dataSourceId)
        }
    }

    @EntityGraph("user.roles")
    fun findById(id: Long): User {
        return userRepository.findById(id).orElseThrow { UserNotFoundException() }
    }

    fun getByUid(uid: String): User? {
        return userRepository.getByUid(uid)
    }

    fun getByUsosId(usosId: Long): User? {
        return userRepository.getByUsosId(usosId)
    }

    fun save(user: User): User {
        val ldapUser = getLdapUserByUid(user.uid)
        user.apply {
            usosId = ldapUser.usosId
            givenName = ldapUser.givenName
            surname = ldapUser.surname
            email = ldapUser.email
        }
        return userRepository.save(user)
    }

    fun update(user: User): User {
        println(user.roles)
        val id = user.id ?: throw IllegalArgumentException("User id is null")
        userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("User not found")
        val ldapUser = getLdapUserByUid(user.uid)
        user.apply {
            usosId = ldapUser.usosId
            givenName = ldapUser.givenName
            surname = ldapUser.surname
            email = ldapUser.email
        }
        return userRepository.save(user)
    }

    fun delete(id: Long) {
        val user = userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("User not found")
        return userRepository.delete(user)
    }

    fun getAll(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }

    fun getLdapUserByUid(uid: String) = ldapUserRepository.findByUid(uid) ?: throw UidNotFoundException()
}
