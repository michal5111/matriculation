package pl.poznan.ue.matriculation.local.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Role(

    @Id
    val code: String,

    val name: String,

    @JsonIgnore
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    val userRoles: MutableSet<UserRole>


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Role

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}