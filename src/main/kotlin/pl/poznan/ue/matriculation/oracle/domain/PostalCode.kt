package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_KODY_POCZTOWE")
class PostalCode(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_KOD_POCZ_SEQ")
    @SequenceGenerator(sequenceName = "DZ_KOD_POCZ_SEQ", allocationSize = 1, name = "DZ_KOD_POCZ_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Column(name = "KOD", length = 5, nullable = false)
    val code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PW_KOD", referencedColumnName = "KOD", nullable = true)
    val county: County? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GM_KOD", referencedColumnName = "KOD", nullable = true)
    val commune: Commune? = null,

    @Column(name = "POCZTA", length = 100, nullable = true)
    val post: String,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_MIASTO", length = 1, nullable = true)
    val cityIsCity: Boolean?
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostalCode

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
