package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.Immutable
import javax.persistence.*

@Entity
@Immutable
@Table(name = "DZ_REGIONY_NUTS")
class NUTSRegion(
    @Id
    @Column(name = "KOD", length = 10, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OB_KOD", referencedColumnName = "KOD", nullable = false)
    val citizenship: Citizenship,

    @Column(name = "POZIOM", length = 1, nullable = false)
    val level: String,

    @OneToMany(mappedBy = "nutsRegion", fetch = FetchType.LAZY)
    val schools: Set<School>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NUTSRegion

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
