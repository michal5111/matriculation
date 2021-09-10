package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_UPRAWNIENIA_DO_KIERUNKOW")
class FieldOfStudyPermission(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_UPR_KIER_SEQ")
    @SequenceGenerator(sequenceName = "DZ_UPR_KIER_SEQ", allocationSize = 1, name = "DZ_UPR_KIER_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Column(name = "KOD_POLON", length = 20, nullable = false)
    val polonCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
    val organizationalUnit: OrganizationalUnit,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KRSTD_KOD", referencedColumnName = "KOD")
    val fieldOfStudy: FieldOfStudy,

    @Column(name = "STOPIEN_STUDIOW", length = 1, nullable = false)
    val degreeOfStudy: Int,

    @Column(name = "PROFIL", length = 1, nullable = true)
    val profile: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STOP_ZAW_ID", referencedColumnName = "ID")
    val professionalDegree: ProfessionalDegree,

    @Column(name = "KOMENTARZ", length = 200, nullable = true)
    val comment: String? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_AKTUALNE", length = 1, nullable = false)
    val isCurrent: Boolean = true,

    @Column(name = "POPRZEDNI_KOD_POLON", length = 20, nullable = true)
    val previousPolonCode: String? = null,

    @OneToMany(mappedBy = "fieldOfStudyPermission", fetch = FetchType.LAZY)
    val conductedFieldsOfStudy: MutableList<ConductedFieldOfStudy>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FieldOfStudyPermission

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
