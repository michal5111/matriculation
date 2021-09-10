package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_TYPY_JEDNOSTEK")
class UnitType(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = false)
    val description: String,

    @OneToMany(mappedBy = "unitType", fetch = FetchType.LAZY)
    val organizationalUnits: Set<OrganizationalUnit>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnitType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
