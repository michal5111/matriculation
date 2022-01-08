package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import javax.persistence.*

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_TYPY_DOKUMENTOW")
class DocumentType(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_AKTUALNY", length = 1, nullable = false)
    val isCurrent: Boolean = true,

    @OneToMany(mappedBy = "documentType", fetch = FetchType.LAZY)
    val personDocuments: MutableList<PersonDocument>,

    @OneToMany(mappedBy = "documentType", fetch = FetchType.LAZY)
    val ownedDocuments: MutableList<OwnedDocument>,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
