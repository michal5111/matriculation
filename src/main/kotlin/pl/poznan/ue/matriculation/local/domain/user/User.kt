package pl.poznan.ue.matriculation.local.domain.user

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import pl.poznan.ue.matriculation.local.serializer.CustomRolesSerializer
import javax.persistence.*

@Entity
class User(

    @Column(unique = true)
    val uid: String,

    @JsonSerialize(using = CustomRolesSerializer::class)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    var roles: MutableSet<UserRole> = mutableSetOf()


) : BaseEntityLongId() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}