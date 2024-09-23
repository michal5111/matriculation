package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_MAG_DOSTAWCY")
class WarehouseSuppliers(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_MAG_DOST_SEQ")
    @SequenceGenerator(sequenceName = "DZ_MAG_DOST_SEQ", allocationSize = 1, name = "DZ_MAG_DOST_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Column(name = "NAZWA_SKROCONA", length = 40, nullable = false)
    val shortName: String,

    @Column(name = "NAZWA_PELNA", length = 255, nullable = true)
    val fullName: String? = null,

    @Column(name = "NIP", length = 13, nullable = true)
    val nip: String? = null,

    @Column(name = "KOMENTARZ", length = 2000, nullable = true)
    val comment: String? = null,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WarehouseSuppliers

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
