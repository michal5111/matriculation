package pl.poznan.ue.matriculation.local.dto

data class UserDto(
    val id: Long?,
    val uid: String,
    val roles: List<RoleDto>
)
