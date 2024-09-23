package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.user.User
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

    @EntityGraph("user.roles")
    override fun findById(id: Long): Optional<User>

    @EntityGraph("user.roles")
    fun getByUid(uid: String): User?

    @EntityGraph("user.roles")
    fun getByUsosId(usosId: Long): User?

    override fun findAll(pageable: Pageable): Page<User>
}
