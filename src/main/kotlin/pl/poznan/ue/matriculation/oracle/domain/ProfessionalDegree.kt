package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_STOPNIE_ZAWODOWE")
class ProfessionalDegree(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_STZA_SEQ")
    @SequenceGenerator(sequenceName = "DZ_STZA_SEQ", allocationSize = 1, name = "DZ_STZA_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Column(name = "KOD", length = 20, nullable = true)
    val code: String,

    @Column(name = "NAZWA", length = 100, nullable = false)
    val name: String,

    @Column(name = "NAZWA_W_DOPELNIACZU", length = 100, nullable = true)
    val genitiveName: String? = null,

    @Column(name = "KOD_POLON", length = 50, nullable = false)
    val polonCode: String,

    @OneToMany(mappedBy = "professionalDegree", fetch = FetchType.LAZY)
    val fieldOfStudyPermissions: MutableList<FieldOfStudyPermission>,

    @OneToMany(mappedBy = "professionalDegree", fetch = FetchType.LAZY)
    var conductedFieldOfStudy: MutableList<ConductedFieldOfStudy>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfessionalDegree

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
