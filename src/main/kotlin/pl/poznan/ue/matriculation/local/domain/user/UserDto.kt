package pl.poznan.ue.matriculation.local.domain.user

interface UserDto {
    val version: Long
    val id: Long?
    val uid: String?
    val givenName: String?
    val surname: String?
    val email: String?
    val roles: MutableSet<RoleDto>
    val usosId: Long?
}
