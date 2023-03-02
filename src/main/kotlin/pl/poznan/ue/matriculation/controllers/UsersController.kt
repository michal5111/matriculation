package pl.poznan.ue.matriculation.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import pl.poznan.ue.matriculation.kotlinExtensions.toPageDto
import pl.poznan.ue.matriculation.kotlinExtensions.toUserDto
import pl.poznan.ue.matriculation.local.domain.user.User
import pl.poznan.ue.matriculation.local.dto.PageDto
import pl.poznan.ue.matriculation.local.dto.UserDto
import pl.poznan.ue.matriculation.local.service.UserService

@RestController
@RequestMapping("/api/users")
class UsersController(
    private val userService: UserService
) {
    @PostMapping
    fun createUser(@RequestBody user: User): User = userService.save(user)

    @PutMapping
    fun updateUser(@RequestBody user: User): User = userService.update(user)

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable("id") id: Long) = userService.delete(id)

    @GetMapping
    fun getAllUsers(pageable: Pageable): PageDto<UserDto> = userService.getAll(pageable).toPageDto {
        UserDto(
            id = it.id,
            uid = it.uid,
            roles = emptyList(),
            givenName = it.givenName,
            surname = it.surname,
            email = it.email,
            usosId = it.usosId,
            version = it.version
        )
    }

    @GetMapping("/{id}")
    fun findUserById(@PathVariable("id") id: Long) = userService.findById(id).toUserDto()
}
