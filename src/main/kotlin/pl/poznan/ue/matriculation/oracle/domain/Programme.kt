package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import java.util.*
import javax.persistence.*

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_PROGRAMY")
class Programme(

    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_OD", nullable = false)
    val dateFrom: Date,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_DO", nullable = true)
    val dateTo: Date? = null,

    @Column(name = "TRYB_STUDIOW", length = 100, nullable = true)
    val studyMode: String? = null,

    @Column(name = "RODZAJ_STUDIOW", length = 100, nullable = true)
    val typeOfStudies: String? = null,

    @Column(name = "CZAS_TRWANIA", length = 100, nullable = true)
    val duration: String? = null,

    @Column(name = "DESCRIPTION", length = 200, nullable = true)
    val descriptionEng: String? = null,

    @Column(name = "DALSZE_STUDIA", length = 200, nullable = true)
    val furtherStudies: String? = null,

    @Column(name = "DALSZE_STUDIA_ANG", length = 200, nullable = true)
    val furtherStudiesEng: String? = null,

    @Column(name = "RODZAJ_STUDIOW_ANG", length = 100, nullable = true)
    val typeOfStudiesEng: String? = null,

    @Column(name = "CZAS_TRWANIA_ANG", length = 100, nullable = true)
    val durationEng: String? = null,

    @Column(name = "TRYB_STUDIOW_ANG", length = 100, nullable = true)
    val studyModeEng: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TCDYD_KOD", referencedColumnName = "KOD", nullable = true)
    val didacticCycleType: DidacticCycleType? = null,

    @Column(name = "LICZBA_JEDN", length = 10, nullable = false)
    val unitsNumber: Long? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
    val display: Boolean = true,

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "UPRAWNIENIA_ZAWODOWE", nullable = true)
    @Lob
    val professionalQualifications: String? = null,

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "UPRAWNIENIA_ZAWODOWE_ANG", nullable = true)
    @Lob
    val professionalQualificationsEng: String? = null,

    @Column(name = "OPIS_NIE", length = 200, nullable = true)
    val descriptionGer: String? = null,

    @Column(name = "OPIS_ROS", length = 200, nullable = true)
    val descriptionRus: String? = null,

    @Column(name = "OPIS_HIS", length = 200, nullable = true)
    val descriptionHis: String? = null,

    @Column(name = "OPIS_FRA", length = 200, nullable = true)
    val descriptionFra: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KONF_SR_KOD", referencedColumnName = "KOD")
    val arithmeticAverageConfiguration: ArithmeticAverageConfiguration,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROW_KIER_ID", referencedColumnName = "ID")
    val conductedFieldOfStudy: ConductedFieldOfStudy,

    @Column(name = "PROFIL", length = 1, nullable = true)
    val profile: Char? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_STUDIA_MIEDZYOBSZAROWE", length = 1, nullable = false)
    val isInternationalStudies: Boolean = false,

    @Column(name = "CZY_BEZPLATNY_USTAWA")
    val isFreeAct: Char? = null,

    @Column(name = "LIMIT_ECTS", length = 15, nullable = true)
    val ectsLimit: Double? = null,

    @Column(name = "DODATKOWE_ECTS_USTAWA", length = 15, nullable = false)
    val additionalEctsAct: Double = 0.0,

    @Column(name = "DODATKOWE_ECTS_UCZELNIA", length = 15, nullable = false)
    val additionalEctsUniversity: Double = 0.0,

    @Column(name = "CZYNNIKI_SZKODLIWE", length = 4000, nullable = true)
    val harmfulFactors: String? = null,

    @Column(name = "ZAKRES", length = 500, nullable = true)
    val range: String? = null,

    @Column(name = "ZAKRES_ANG", length = 500, nullable = true)
    val rangeEng: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD_PODST", referencedColumnName = "KOD", nullable = true)
    val organizationalUnitPrimary: OrganizationalUnit? = null,

    @Column(name = "KOD_POLON_ISM", length = 20, nullable = true)
    val polonCodeIsm: String? = null,

    @Column(name = "KOD_POLON_DR", length = 20, nullable = true)
    val polonCodeDr: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KOD_ISCED", referencedColumnName = "KOD", nullable = true)
    val iscedCode: IscedCode? = null,

    @Column(name = "KOD_POLON_REKRUTACJA", length = 20, nullable = true)
    val polonCodeRegistration: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD_PROW", referencedColumnName = "KOD", nullable = true)
    val organizationalUnitLeading: OrganizationalUnit? = null,

    @Column(name = "USTAL_DATE_KONCA_STUDIOW", length = 1, nullable = false)
    val setEndOfStudyDate: Char = 'D',

    @Column(name = "UID_POLON_DR", length = 128, nullable = true)
    val polonUidDr: String? = null,

    @OneToMany(mappedBy = "programme", fetch = FetchType.LAZY)
    var personProgrammes: MutableList<PersonProgramme>,

    @OneToMany(mappedBy = "programme", fetch = FetchType.LAZY)
    var programmeStages: MutableList<ProgrammeStage>,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Programme

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
