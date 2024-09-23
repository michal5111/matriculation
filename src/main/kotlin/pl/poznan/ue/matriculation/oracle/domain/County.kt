package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_POWIATY")
class County(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = false)
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WJ_KOD", referencedColumnName = "KOD")
    val voivodeship: Voivodeship,

    @OneToMany(mappedBy = "county", fetch = FetchType.LAZY)
    val addresses: MutableList<Address>,

    @OneToMany(mappedBy = "county", fetch = FetchType.LAZY)
    val communes: List<Commune>,

    @OneToMany(mappedBy = "county", fetch = FetchType.LAZY)
    val postalCodes: MutableList<PostalCode>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as County

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
