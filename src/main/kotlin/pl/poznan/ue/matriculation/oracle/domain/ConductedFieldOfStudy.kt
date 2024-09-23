package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_PROWADZONE_KIERUNKI")
class ConductedFieldOfStudy(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_PROW_KIER_SEQ")
    @SequenceGenerator(sequenceName = "DZ_PROW_KIER_SEQ", allocationSize = 1, name = "DZ_PROW_KIER_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Column(name = "KOD_POLON", length = 20, nullable = false)
    val polonCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD", nullable = false)
    val organizationalUnit: OrganizationalUnit,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KRSTD_KOD", referencedColumnName = "KOD", nullable = false)
    val fieldOfStudy: FieldOfStudy,

    @Column(name = "STOPIEN_STUDIOW", length = 1, nullable = false)
    val degreeOfStudy: Int,

    @Column(name = "FORMA_STUDIOW", length = 2, nullable = false)
    val formOfStudy: String = "SN",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPR_KIER_ID", referencedColumnName = "ID", nullable = false)
    val fieldOfStudyPermission: FieldOfStudyPermission,

    @Column(name = "PROFIL", length = 2, nullable = false)
    val profile: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KRSTD_KOD_SPEC", referencedColumnName = "KOD", nullable = true)
    val fieldOfStudySpeciality: FieldOfStudy,

    @Column(name = "UID_POLON", length = 128, nullable = true)
    val polonUID: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JZK_KOD_KSZT", referencedColumnName = "KOD", nullable = true)
    val language: Language,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STOP_ZAW_ID", referencedColumnName = "ID", nullable = true)
    val professionalDegree: ProfessionalDegree,

    @Column(name = "LICZBA_SEMESTROW", length = 2, nullable = true)
    val numberOfSemesters: Int? = null,

    @Column(name = "PUNKTY_ECTS", length = 13, nullable = true)
    val EctsPoints: Double?,

    @OneToMany(mappedBy = "conductedFieldOfStudy", fetch = FetchType.LAZY)
    val programmes: MutableList<Programme>,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConductedFieldOfStudy

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
