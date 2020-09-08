package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.user.Role

@Repository
interface RoleRepository : JpaRepository<Role, String>