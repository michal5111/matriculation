package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_TYPY_INDEKSOW")
class IndexType(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = false)
    val description: String,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_LICZBOWY", length = 1, nullable = false)
    val isNumeric: Boolean = false,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_UNIKATOWY", length = 1, nullable = false)
    val isUnique: Boolean = false,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_Z_PREFIKSEM", length = 1, nullable = false)
    val isWithPrefix: Boolean = false,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_PULA_CENTRALNA", length = 1, nullable = false)
    val isCentralPool: Boolean = false,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_AKTUALNY", length = 1, nullable = false)
    val isCurrent: Boolean = true,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "indexType")
    val students: MutableList<Student>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IndexType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
