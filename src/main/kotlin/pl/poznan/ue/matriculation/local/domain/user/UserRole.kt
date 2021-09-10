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
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserRole) return false

        if (user != other.user) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}
