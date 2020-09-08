package pl.poznan.ue.matriculation.local.domain.user

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Role(

        @Id
        val code: String,

        val name: String,

        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        val userRoles: MutableList<UserRole>
)