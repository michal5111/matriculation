package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*

@Entity
@Table(name = "DZ_UPRAWNIENIA_DO_KIERUNKOW")
data class FieldOfStudyPermission(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_UPR_KIER_SEQ")
        @SequenceGenerator(sequenceName = "DZ_UPR_KIER_SEQ", allocationSize = 1, name = "DZ_UPR_KIER_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @Column(name = "KOD_POLON", length = 20, nullable = false)
        var polonCode: String,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
        val organizationalUnit: OrganizationalUnit,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRSTD_KOD", referencedColumnName = "KOD")
        var fieldOfStudy: FieldOfStudy,

        @Column(name = "STOPIEN_STUDIOW", length = 1, nullable = false)
        var degreeOfStudy: Int,

        @Column(name = "PROFIL", length = 1, nullable = true)
        var profile: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "STOP_ZAW_ID", referencedColumnName = "ID")
        var professionalDegree: ProfessionalDegree,

        @Column(name = "KOMENTARZ", length = 200, nullable = true)
        var comment: String? = null,

        @Column(name = "CZY_AKTUALNE", length = 1, nullable = false)
        var isCurrent: Char = 'T',

        @Column(name = "POPRZEDNI_KOD_POLON", length = 20, nullable = true)
        var previousPolonCode: String? = null,

        @OneToMany(mappedBy = "fieldOfStudyPermission", fetch = FetchType.LAZY)
        var conductedFieldsOfStudy: MutableList<ConductedFieldOfStudy>
)