package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_PRZYJAZDY")
class Arrival(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_PZD_SEQ")
        @SequenceGenerator(sequenceName = "DZ_PZD_SEQ", allocationSize = 1, name = "DZ_PZD_SEQ")
        @Column(name = "ID", length = 10)
        var id: Long? = null,

        @Column(name = "DATA_POCZ", nullable = false)
        var startDate: Date,

        @Column(name = "DATA_KON", nullable = false)
        var endDate: Date,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = true)
        var person: Person? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "UMW_ID", referencedColumnName = "ID", nullable = true)
        var contract: Contract? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "WSP_ID", referencedColumnName = "ID", nullable = true)
        var cooperation: Cooperation? = null,

        @Column(name = "STAN_OSOBY", nullable = true)
        var personState: Int? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PRAC_ID_OPIEKUN_ORG", referencedColumnName = "ID", nullable = true)
        var organizationalSupervisorEmployee: Employee? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PRAC_ID_OPIEKUN_NAUK", referencedColumnName = "ID", nullable = true)
        var researchTutorEmployee: Employee? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
        var school: School? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "AKADEMIK_ID", referencedColumnName = "ID", nullable = true)
        var dormitory: Dormitory? = null,

        @Column(name = "ZAKW_MIASTO", nullable = true, length = 1)
        var hasOwnAccommodation: Char? = 'N',

        @Column(name = "UWAGI", nullable = true, length = 240)
        var comments: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KOD_SOK", referencedColumnName = "KOD")
        var erasmusCode: ErasmusCode? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CDYD_KOD", referencedColumnName = "KOD", nullable = false)
        var didacticCycleAcademicYear: DidacticCycle,

        @Column(name = "TYP_PRZYJAZDU", nullable = false, length = 2)
        var arrivalType: String,

        @Column(name = "PLAN_CZAS_POBYTU", nullable = true, length = 1)
        var stayTimePlan: Char? = null,

        @Column(name = "ROK_STUDIOW", nullable = true, length = 1)
        var studiesYear: Int? = null,

        @Column(name = "CZY_POLONICUM", nullable = true, length = 1)
        var polonicum: Char? = null,

        @Column(name = "DATA_PRZYJAZDU", nullable = true)
        var arrivalDate: Date? = null,

        @Column(name = "DATA_WYJAZDU", nullable = true)
        var leavingDate: Date? = null,

        @Column(name = "PRZEDLUZENIE", nullable = true)
        var extension: Date? = null,

        @Column(name = "NUMER_TECZKI", nullable = true, length = 200)
        var fileNumber: String? = null,

        @Column(name = "NUMER", nullable = true, length = 10)
        var number: Int? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ATR_WYM_PRZEDL_ID", referencedColumnName = "ID", nullable = true)
        var extensionReasonExchangeAttribute: ExchangeAttribute? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ATR_WYM_RODZ_ID", referencedColumnName = "ID", nullable = true)
        var typeOfStudiesExchangeAttribute: ExchangeAttribute? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ATR_WYM_FORMA_ID", referencedColumnName = "ID", nullable = true)
        var formOfEducationExchangeAttribute: ExchangeAttribute? = null,

        @Column(name = "CZY_ZAKWATEROWANIE", nullable = true, length = 1)
        var wantAccommodation: Char?,

        @Column(name = "LICZBA_MIES_STYP", nullable = true, length = 4, precision = 2)
        var scholarshipMonthsCount: Float? = null,

        @Column(name = "LICZBA_MIES_BEZ_STYP", nullable = true, length = 4, precision = 2)
        var noScholarshipMonthsCount: Float? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "WYJ_GRP_KOD", referencedColumnName = "KOD", nullable = true)
        var departureGroup: DepartureGroup? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "FIN_CDYD_KOD", referencedColumnName = "KOD", nullable = false)
        var financingDidacticCycleAcademicYear: DidacticCycle,

        @Column(name = "ID_EWP", nullable = true, length = 64)
        var ewpID: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KOD_ISCED", referencedColumnName = "KOD", nullable = true)
        var iscedCode: IscedCode? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID", nullable = true)
        var personProgramme: PersonProgramme?,

        @Column(name = "CZY_WSPOLNE_STUDIA", nullable = true, length = 1)
        var isJointStudies: Char? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KPS_DOKOS_ID", referencedColumnName = "ID", nullable = true)
        var personDocument: PersonDocument? = null
)