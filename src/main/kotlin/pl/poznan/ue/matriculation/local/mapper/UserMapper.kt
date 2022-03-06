package pl.poznan.ue.matriculation.local.mapper

import pl.poznan.ue.matriculation.local.domain.user.User
import pl.poznan.ue.matriculation.local.dto.RoleDto
import pl.poznan.ue.matriculation.local.dto.UserDto

class UserMapper {

    fun mapUserToUserDto(user: User): UserDto {
        return UserDto(
            id = user.id,
            uid = user.uid,
            roles = user.roles.map { RoleDto(it.role.code, it.role.name) }
        )
    }
}
