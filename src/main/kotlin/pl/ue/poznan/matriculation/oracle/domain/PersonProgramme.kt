package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_PROGRAMY_OSOB")
data class PersonProgramme(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_PRGOS_SEQ")
        @SequenceGenerator(sequenceName = "DZ_PRGOS_SEQ", allocationSize = 1, name = "DZ_PRGOS_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID",referencedColumnName = "ID", nullable = false)
        var person: Person,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PRG_KOD", referencedColumnName = "KOD", nullable = false)
        var programme: Programme,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ST_ID", referencedColumnName = "ID", nullable = false)
        var student: Student,

        @Column(name = "CZY_GLOWNY", length = 1, nullable = true)
        var isDefault: Char? = 'N',

        @Column(name = "DATA_NAST_ZAL", nullable = true)
        var dateToNextPass: Date? = null,

        @Column(name = "UPRAWNIENIA_ZAWODOWE", length = 4000, nullable = true)
        var professionalPowers: String? = null,

        @Column(name = "UPRAWNIENIA_ZAWODOWE_ANG", length = 4000, nullable = true)
        var professionalPowersEng: String? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD", nullable = true)
        var organizationalUnit: OrganizationalUnit? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DOK_UPR_ID", referencedColumnName = "ID")
        var entitlementDocument: EntitlementDocument? = null,

        @Column(name = "DATA_PRZYJECIA", nullable = true)
        var dateOfAddmision: Date? = null,

        @Column(name = "PLAN_DATA_UKON", nullable = true)
        var plannedDateOfCompletion: Date? = null,

        @Column(name = "CZY_ZGLOSZONY", length = 1, nullable = false)
        var isReported: Char = 'N',

        @Column(name = "STATUS", length = 6, nullable = false)
        var status: String = "STU",

        @Column(name = "DATA_ROZPOCZECIA", nullable = true)
        var startDate: Date,

        @Column(name = "NUMER_S", length = 10, nullable = true)
        var certificateNumberNumeric: Int? = null,

        @Column(name = "NUMER_SWIADECTWA", length = 10, nullable = true)
        var certificateNumberString: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TECZ_ID", referencedColumnName = "ID")
        var folder: Folder? = null,

        @Column(name = "DATA_ARCH", nullable = true)
        var archiveDate: Date? = null,

        @Column(name = "WARUNKI_PRZYJEC_NA_PROG", length = 2000, nullable = true)
        var programmeAdmissionConditions: String? = null,

        @Column(name = "WARUNKI_PRZYJEC_NA_PROG_ANG", length = 2000, nullable = true)
        var programmeAdmissionConditionsEng: String? = null,

        @Column(name = "NUMER_DO_BANKU", length = 10, nullable = true)
        var numberToBankNumeric: Int? = null,

        @Column(name = "NUMER_DO_BANKU_SYGN", length = 50, nullable = true)
        var numberToBankString: String? = null,

        @Column(name = "NUMER_5_PROC", length = 10, nullable = true)
        var numberFivePercentNumeric: Int? = null,

        @Column(name = "NUMER_5_PROC_SYGN", length = 10, nullable = true)
        var numberFivePercentString: String? = null,

        @Column(name = "STATUS_ARCH", length = 1, nullable = false)
        var archiveStatus: Char = 'N',

        @Column(name = "OSIAGNIECIA", nullable = true)
        @Lob
        var achievements: String? = null,

        @Column(name = "OSIAGNIECIA_ANG", nullable = true)
        @Lob
        var achievementsEng: String? = null,

        @Column(name = "NR_KIERUNKU_USTAWA", length = 1, nullable = true)
        var fieldOfStudyNumberAct: Char? = null,

        @Column(name = "LIMIT_ECTS", length = 15, nullable = true)
        var ectsLimit: Double? = null,

        @Column(name = "DODATKOWE_ECTS_UCZELNIA", length = 15, nullable = false)
        var additionalEctsUniversity: Double = 0.0,

        @Column(name = "WYKORZYSTANE_ECTS_OBCE", length = 15, nullable = false)
        var usedEctsForeign: Double = 0.0,

        @Column(name = "LIMIT_ECTS_PODPIECIA", length = 15, nullable = false)
        var ectsLimitAttaching: Double? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID")
        var personProgramme: PersonProgramme? = null,

        @Column(name = "OSIAGNIECIA_PROGRAMU", length = 4000, nullable = true)
        var programmeAchievements: String? = null,

        @Column(name = "OSIAGNIECIA_PROGRAMU_ANG", length = 4000, nullable = true)
        var programmeAchievementsEng: String? = null,

        @Column(name = "WYNIK_STUDIOW", length = 100, nullable = true)
        var studyScore: String? = null,

        @Column(name = "WYNIK_STUDIOW_ANG", length = 100, nullable = true)
        var studyScoreEng: String? = null,

        @Column(name = "UMOWA_DATA_PRZECZYTANIA", nullable = true)
        var agreementReadDate: Date? = null,

        @Column(name = "UMOWA_DATA_PODPISANIA", nullable = true)
        var agreementSignDate: Date? = null,

        @Column(name = "UMOWA_SYGNATURA", length = 20, nullable = true)
        var agreementSignature: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KOD_ISCED", referencedColumnName = "KOD")
        var iscedCode: IscedCode? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PODST_PODJ_STD_KOD", referencedColumnName = "KOD")
        var groundsForUndertakingStudies: GroundsForUndertakingStudies? = null,

        @OneToMany(mappedBy = "personProgramme" ,fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        val personStages: MutableList<PersonStage> = mutableListOf()
)