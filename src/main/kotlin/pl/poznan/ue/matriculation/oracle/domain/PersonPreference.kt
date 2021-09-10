package pl.poznan.ue.matriculation.oracle.domain

import java.io.Serializable
import javax.persistence.*

@Entity
@IdClass(PersonPreferenceId::class)
@Table(name = "DZ_PREFERENCJE_OSOB")
class PersonPreference(

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false, unique = false)
    var person: Person,

    @Id
    @Column(name = "ATRYBUT", length = 40, nullable = false)
    var attribute: String,

    @Column(name = "WARTOSC", length = 40, nullable = true)
    var value: String? = null
) : BaseEntity(), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonPreference

        if (person != other.person) return false

        return true
    }

    override fun hashCode(): Int {
        return person.hashCode()
    }
}
