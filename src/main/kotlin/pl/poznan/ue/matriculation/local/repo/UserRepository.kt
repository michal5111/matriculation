package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.user.User

@Repository
interface UserRepository : PagingAndSortingRepository<User, Long> {

    fun getByUid(uid: String): User?

    fun getByUsosId(usosId: Long): User?
}