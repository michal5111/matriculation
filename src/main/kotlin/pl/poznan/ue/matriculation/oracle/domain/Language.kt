package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import javax.persistence.*

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_JEZYKI")
class Language(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "NAZWA", length = 30, nullable = false)
    val name: String,

    @Column(name = "NAZWA_ANG", length = 100, nullable = false)
    val nameEng: String,

    @Column(name = "KOD_ISO6391", length = 2, nullable = true)
    val Iso6391Code: String? = name,

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    val conductedFieldsOfStudy: MutableList<ConductedFieldOfStudy>,

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    val personDocuments: MutableList<PersonDocument>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Language

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
