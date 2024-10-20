package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_AKADEMIKI")
class Dormitory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_AKADEMIKI_SEQ")
    @SequenceGenerator(sequenceName = "DZ_AKADEMIKI_SEQ", allocationSize = 1, name = "DZ_AKADEMIKI_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Column(name = "NAZWA", length = 40, nullable = false)
    val name: String,

    @Column(name = "OPIS", length = 2000, nullable = true)
    val description: String? = null,

    @Column(name = "SKROT", length = 5, nullable = true)
    val short: String? = null,

    @Column(name = "OPIS_ANG", length = 2000, nullable = true)
    val englishDescription: String? = null,

    @OneToOne(mappedBy = "dormitory", fetch = FetchType.LAZY)
    val address: Address,

    @OneToMany(mappedBy = "dormitory", fetch = FetchType.LAZY)
    val phoneNumbers: List<PhoneNumber>,

    @OneToMany(mappedBy = "dormitory", fetch = FetchType.LAZY)
    val arrivals: List<Arrival>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dormitory

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
