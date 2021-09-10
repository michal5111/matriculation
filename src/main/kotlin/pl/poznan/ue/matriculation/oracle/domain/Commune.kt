package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_GMINY")
class Commune(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "NAZWA", length = 100, nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PW_KOD", referencedColumnName = "KOD", nullable = false)
    val county: County,

    @OneToMany(mappedBy = "commune", fetch = FetchType.LAZY)
    val address: MutableList<Address>,

    @OneToMany(mappedBy = "commune", fetch = FetchType.LAZY)
    val postalCodes: MutableList<PostalCode>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Commune

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
