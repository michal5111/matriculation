package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import java.util.*

@Entity
@Table(name = "DZ_PRZYJAZDY")
class Arrival(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_PZD_SEQ")
    @SequenceGenerator(sequenceName = "DZ_PZD_SEQ", allocationSize = 1, name = "DZ_PZD_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_POCZ", nullable = false)
    val startDate: Date,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_KON", nullable = false)
    val endDate: Date,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = true)
    var person: Person? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UMW_ID", referencedColumnName = "ID", nullable = true)
    val contract: Contract? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WSP_ID", referencedColumnName = "ID", nullable = true)
    val cooperation: Cooperation? = null,

    @Column(name = "STAN_OSOBY", nullable = true)
    val personState: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRAC_ID_OPIEKUN_ORG", referencedColumnName = "ID", nullable = true)
    val organizationalSupervisorEmployee: Employee? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRAC_ID_OPIEKUN_NAUK", referencedColumnName = "ID", nullable = true)
    val researchTutorEmployee: Employee? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
    val school: School? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AKADEMIK_ID", referencedColumnName = "ID", nullable = true)
    val dormitory: Dormitory? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "ZAKW_MIASTO", nullable = true, length = 1)
    val hasOwnAccommodation: Boolean = false,

    @Column(name = "UWAGI", nullable = true, length = 240)
    val comments: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KOD_SOK", referencedColumnName = "KOD")
    val erasmusCode: ErasmusCode? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CDYD_KOD", referencedColumnName = "KOD", nullable = false)
    val didacticCycleAcademicYear: DidacticCycle,

    @Column(name = "TYP_PRZYJAZDU", nullable = false, length = 2)
    val arrivalType: String = "S",

    @Column(name = "PLAN_CZAS_POBYTU", nullable = true, length = 1)
    val stayTimePlan: Char? = null,

    @Column(name = "ROK_STUDIOW", nullable = true, length = 1)
    val studiesYear: Int? = null,

    @Column(name = "CZY_POLONICUM", nullable = true, length = 1)
    val polonicum: Char? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_PRZYJAZDU", nullable = true)
    val arrivalDate: Date? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_WYJAZDU", nullable = true)
    val leavingDate: Date? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "PRZEDLUZENIE", nullable = true)
    val extension: Date? = null,

    @Column(name = "NUMER_TECZKI", nullable = true, length = 200)
    val fileNumber: String? = null,

    @Column(name = "NUMER", nullable = true, length = 10)
    val number: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATR_WYM_PRZEDL_ID", referencedColumnName = "ID", nullable = true)
    val extensionReasonExchangeAttribute: ExchangeAttribute? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATR_WYM_RODZ_ID", referencedColumnName = "ID", nullable = true)
    val typeOfStudiesExchangeAttribute: ExchangeAttribute? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATR_WYM_FORMA_ID", referencedColumnName = "ID", nullable = true)
    val formOfEducationExchangeAttribute: ExchangeAttribute? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_ZAKWATEROWANIE", nullable = true, length = 1)
    val wantAccommodation: Boolean?,

    @Column(name = "LICZBA_MIES_STYP", nullable = true, length = 4, precision = 2)
    val scholarshipMonthsCount: Float? = null,

    @Column(name = "LICZBA_MIES_BEZ_STYP", nullable = true, length = 4, precision = 2)
    val noScholarshipMonthsCount: Float? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WYJ_GRP_KOD", referencedColumnName = "KOD", nullable = true)
    val departureGroup: DepartureGroup? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FIN_CDYD_KOD", referencedColumnName = "KOD", nullable = false)
    val financingDidacticCycleAcademicYear: DidacticCycle,

    @Column(name = "ID_EWP", nullable = true, length = 64)
    val ewpID: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KOD_ISCED", referencedColumnName = "KOD", nullable = true)
    val iscedCode: IscedCode? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID", nullable = true)
    var personProgramme: PersonProgramme? = null,

    @Column(name = "CZY_WSPOLNE_STUDIA", nullable = true, length = 1)
    val isJointStudies: Char? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KPS_DOKOS_ID", referencedColumnName = "ID", nullable = true)
    val personDocument: PersonDocument? = null,
) : BaseEntity()
