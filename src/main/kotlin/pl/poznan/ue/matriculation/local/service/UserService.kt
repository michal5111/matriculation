package pl.poznan.ue.matriculation.local.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.UidNotFoundException
import pl.poznan.ue.matriculation.ldap.repo.LdapUserRepository
import pl.poznan.ue.matriculation.local.domain.user.User
import pl.poznan.ue.matriculation.local.domain.user.UserRole
import pl.poznan.ue.matriculation.local.dto.UserDto
import pl.poznan.ue.matriculation.local.repo.RoleRepository
import pl.poznan.ue.matriculation.local.repo.UserRepository
import javax.persistence.EntityNotFoundException

@Service
class UserService(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val ldapUserRepository: LdapUserRepository,
    val roleService: RoleService
) {

    fun getByUid(uid: String): User? {
        return userRepository.getByUid(uid)
    }

    fun getByUsosId(usosId: Long): User? {
        return userRepository.getByUsosId(usosId)
    }

    fun save(userDto: UserDto): User {
        val ldapUser = ldapUserRepository.findByUid(userDto.uid) ?: throw UidNotFoundException()
        val user = User(uid = userDto.uid, usosId = ldapUser.usosId).apply {
            userDto.roles.map { (code) ->
                UserRole(this, roleService.getOne(code))
            }.let {
                roles.addAll(it)
            }
        }
        return userRepository.save(user)
    }

    @Transactional
    fun update(userDto: UserDto): User {
        val id = userDto.id ?: throw IllegalArgumentException("User id is null")
        val user = userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("User not found")
        user.roles.removeIf {
            userDto.roles.none { (code) ->
                it.role.code == code
            }
        }
        userDto.roles.filter {
            user.roles.none { userRole ->
                it.code == userRole.role.code
            }
        }.map {
            UserRole(user, roleRepository.getById(it.code))
        }.let {
            user.roles.addAll(it)
        }
        return user
    }

    @Transactional
    fun delete(id: Long) {
        val user = userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("User not found")
        return userRepository.delete(user)
    }

    fun getAll(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }
}
