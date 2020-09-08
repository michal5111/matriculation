package pl.poznan.ue.matriculation.local.service

import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.domain.user.User
import pl.poznan.ue.matriculation.local.dto.UserDto
import pl.poznan.ue.matriculation.local.repo.RoleRepository
import pl.poznan.ue.matriculation.local.repo.UserRepository
import javax.persistence.EntityNotFoundException

@Service
class UserService(
        val userRepository: UserRepository,
        val roleRepository: RoleRepository
) {

    fun getByUid(uid: String): User? {
        return userRepository.getByUid(uid)
    }

    fun save(user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun update(userDto: UserDto): User {
        val id = userDto.id ?: throw IllegalArgumentException("User id is null")
        val user = userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("User not found")
        user.roles.clear()
        userDto.roles.map {
            roleRepository.getOne(it.code)
        }
        return user
    }

    @Transactional
    fun delete(id: Long) {
        val user = userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("User not found")
        return userRepository.delete(user)
    }

    fun getAll(pageable: org.springframework.data.domain.Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }
}