package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_KONFIG_SREDNICH")
class MediumConfiguration(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 1000, nullable = false)
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TPKT_KOD", referencedColumnName = "KOD")
    var pointType: PointType? = null,

    @Column(name = "CZY_UWZGLEDNIA_NZAL", length = 1, nullable = false)
    var includeNonCreditable: Char,

    @Column(name = "DOKLADNOSC", length = 1, nullable = false)
    var accuracy: Int,

    @Column(name = "SPOSOB_ZAOKRAGLENIA", length = 1, nullable = false)
    var roundWay: Char,

    @Column(name = "DOMYSLNY_STATUS_PODP", length = 1, nullable = false)
    var defaultHookStatus: Char,

    @OneToMany(mappedBy = "mediumConfiguration", fetch = FetchType.LAZY)
    var programmes: MutableList<Programme>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediumConfiguration

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}