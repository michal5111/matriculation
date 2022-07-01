package pl.poznan.ue.matriculation.local.domain.user

import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import javax.persistence.*

@Entity
@NamedEntityGraph(
    name = "user.roles",
    attributeNodes = [NamedAttributeNode(value = "roles")]
)
class User(

    @Column(unique = true)
    val uid: String,

    var givenName: String? = null,

    var surname: String? = null,

    var email: String? = null,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val roles: MutableSet<Role> = HashSet(),

    @Column(unique = true)
    var usosId: Long? = null

) : BaseEntityLongId() {

    fun addRole(role: Role) {
        roles.add(role)
        role.users.add(this)
    }

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

    override fun toString(): String {
        return "User(uid='$uid', givenName=$givenName, surname=$surname, email=$email, usosId=$usosId)"
    }


}
