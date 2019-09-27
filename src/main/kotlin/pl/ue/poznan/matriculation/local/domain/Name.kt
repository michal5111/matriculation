package pl.ue.poznan.matriculation.local.domain

import javax.persistence.*

@Entity
@Table(name = "NAME_STRINGS")
data class Name(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val en: String?,
        val pl: String?
)