package pl.poznan.ue.matriculation.local.domain.user

import java.io.Serializable
import javax.persistence.*

@Entity
class UserRole(

        @Id
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
        val user: User,

        @Id
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "role_code", referencedColumnName = "code", nullable = false)
        val role: Role
) : Serializable