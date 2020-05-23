package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*

@Entity
@Table(name = "DZ_PROWADZONE_KIERUNKI")
class ConductedFieldOfStudy(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_PROW_KIER_SEQ")
        @SequenceGenerator(sequenceName = "DZ_PROW_KIER_SEQ", allocationSize = 1, name = "DZ_PROW_KIER_SEQ")
        @Column(name = "ID", length = 10)
        var id: Long? = null,

        @Column(name = "KOD_POLON", length = 20, nullable = false)
        var polonCode: String,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD", nullable = false)
        val organizationalUnit: OrganizationalUnit,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRSTD_KOD", referencedColumnName = "KOD", nullable = false)
        var fieldOfStudy: FieldOfStudy,

        @Column(name = "STOPIEN_STUDIOW", length = 1, nullable = false)
        var degreeOfStudy: Int,

        @Column(name = "FORMA_STUDIOW", length = 2, nullable = false)
        var formOfStudy: String = "SN",

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "UPR_KIER_ID", referencedColumnName = "ID", nullable = false)
        var fieldOfStudyPermission: FieldOfStudyPermission,

        @Column(name = "PROFIL", length = 2, nullable = false)
        var profile: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRSTD_KOD_SPEC", referencedColumnName = "KOD", nullable = true)
        var fieldOfStudySpeciality: FieldOfStudy,

        @Column(name = "UID_POLON", length = 128, nullable = true)
        var polonUID: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JZK_KOD_KSZT", referencedColumnName = "KOD", nullable = true)
        var language: Language,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "STOP_ZAW_ID", referencedColumnName = "ID", nullable = true)
        var professionalDegree: ProfessionalDegree,

        @Column(name = "LICZBA_SEMESTROW", length = 2, nullable = true)
        var numberOfSemesters: Int? = null,

        @Column(name = "PUNKTY_ECTS", length = 13, nullable = true)
        var EctsPoints: Double?,

        @JsonIgnore
        @OneToMany(mappedBy = "conductedFieldOfStudy")
        var programmes: MutableList<Programme>
) {
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