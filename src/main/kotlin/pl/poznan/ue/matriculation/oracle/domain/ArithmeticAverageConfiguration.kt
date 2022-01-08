package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import javax.persistence.*

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_KONFIG_SREDNICH")
class ArithmeticAverageConfiguration(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 1000, nullable = false)
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TPKT_KOD", referencedColumnName = "KOD")
    val pointType: PointType? = null,

    @Column(name = "CZY_UWZGLEDNIA_NZAL", length = 1, nullable = false)
    val includeNonCreditable: Char,

    @Column(name = "DOKLADNOSC", length = 1, nullable = false)
    val accuracy: Int,

    @Column(name = "SPOSOB_ZAOKRAGLENIA", length = 1, nullable = false)
    val roundWay: Char,

    @Column(name = "DOMYSLNY_STATUS_PODP", length = 1, nullable = false)
    val defaultHookStatus: Char,

    @OneToMany(mappedBy = "arithmeticAverageConfiguration", fetch = FetchType.LAZY)
    val programmes: MutableList<Programme>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArithmeticAverageConfiguration

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
