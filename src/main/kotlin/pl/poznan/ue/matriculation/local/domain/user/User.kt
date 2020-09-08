package pl.poznan.ue.matriculation.local.domain.user

import javax.persistence.*

@Entity
class User(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        val uid: String,

        @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
        val roles: MutableList<UserRole> = mutableListOf()
)