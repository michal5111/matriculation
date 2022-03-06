package pl.poznan.ue.matriculation.local.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import javax.persistence.*

@Entity
@NamedEntityGraph(
    name = "user.roles",
    attributeNodes = [NamedAttributeNode(value = "roles", subgraph = "subgraph.userRole")],
    subgraphs = [NamedSubgraph(
        name = "subgraph.userRole", attributeNodes = [
            NamedAttributeNode("role")
        ]
    )]
)
class User(

    @Column(unique = true)
    val uid: String,

    //@JsonSerialize(using = CustomRolesSerializer::class)
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var roles: MutableSet<UserRole> = HashSet(),

    @Column(unique = true)
    var usosId: Long? = null

) : BaseEntityLongId() {

    fun addRole(role: Role) {
        roles.add(UserRole(this, role))
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
}
