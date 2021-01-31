package pl.poznan.ue.matriculation.local.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.local.domain.user.Role
import pl.poznan.ue.matriculation.local.repo.RoleRepository

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {
    fun getAll(): List<Role> {
        return roleRepository.findAll()
    }

    fun getOne(code: String): Role {
        return roleRepository.getOne(code)
    }
}