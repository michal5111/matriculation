package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_KIERUNKI_STUDIOW")
class FieldOfStudy(

    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NADRZEDNY_KOD", referencedColumnName = "KOD", nullable = false)
    val fieldOfStudy: FieldOfStudy? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYP_KIERUNKU_KOD", referencedColumnName = "KOD", nullable = false)
    val fieldOfStudyType: FieldOfStudyType,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
    val display: Boolean = true,

    @Column(name = "DESCRIPTION", length = 200, nullable = false)
    val descriptionEng: String? = null,

    @Column(name = "OPIS_NIE", length = 200, nullable = true)
    val descriptionGer: String? = null,

    @Column(name = "OPIS_ROS", length = 200, nullable = true)
    val descriptionRus: String? = null,

    @Column(name = "OPIS_HIS", length = 200, nullable = true)
    val descriptionHis: String? = null,

    @Column(name = "OPIS_FRA", length = 200, nullable = true)
    val descriptionFra: String? = null,

    @Column(name = "KOD_POLON", length = 20, nullable = false)
    val polonCode: String,

    @OneToMany(mappedBy = "fieldOfStudy", fetch = FetchType.LAZY)
    val conductedFieldOfStudy: MutableList<ConductedFieldOfStudy>,

    @OneToMany(mappedBy = "fieldOfStudySpeciality", fetch = FetchType.LAZY)
    val conductedFieldOfStudySpeciality: MutableList<ConductedFieldOfStudy>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FieldOfStudy

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
