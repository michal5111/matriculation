package pl.poznan.ue.matriculation.local.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
class UserRole(

        @Id
        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
        val user: User,

        @Id
        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "role_code", referencedColumnName = "code", nullable = false)
        val role: Role
) : Serializable