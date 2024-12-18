package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy

@Entity
//@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "DZ_WKU")
class Wku(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "NAZWA", length = 100, nullable = false)
    val name: String,

    @OneToMany(mappedBy = "wku", fetch = FetchType.LAZY)
    val persons: List<Person>,

    @OneToMany(mappedBy = "wku", fetch = FetchType.LAZY)
    val addresses: List<Address>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Wku

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
