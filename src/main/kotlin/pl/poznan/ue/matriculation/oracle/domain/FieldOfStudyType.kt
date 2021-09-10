package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_TYPY_KIERUNKOW")
class FieldOfStudyType(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = false)
    val description: String,

    @Column(name = "DESCRIPTION", length = 100, nullable = false)
    val descriptionEng: String,

    @Column(name = "OPIS_NIE", length = 100, nullable = true)
    val descriptionGer: String? = null,

    @Column(name = "OPIS_ROS", length = 100, nullable = true)
    val descriptionRus: String? = null,

    @Column(name = "OPIS_HIS", length = 100, nullable = true)
    val descriptionHis: String? = null,

    @Column(name = "OPIS_FRA", length = 100, nullable = true)
    val descriptionFra: String? = null,

    @OneToMany(mappedBy = "fieldOfStudyType", fetch = FetchType.LAZY)
    val fieldsOfStudy: MutableList<FieldOfStudy>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FieldOfStudyType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
