package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_WSPOLPRACE")
class Cooperation(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_WSP_SEQ")
        @SequenceGenerator(sequenceName = "DZ_WSP_SEQ", allocationSize = 1, name = "DZ_WSP_SEQ")
        @Column(name = "ID", nullable = false, updatable = false, length = 10)
        val id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "UMW_ID", nullable = false, referencedColumnName = "ID")
        var contract: Contract,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD", nullable = true)
        var organizationalUnit: OrganizationalUnit?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "POZIOM_STUDIOW_P", referencedColumnName = "ID", nullable = true)
        var arrivingStudiesLevel: ExchangeAttribute?,

        @Column(name = "LICZBA_STUDENTOW_P", nullable = true, length = 10)
        var arrivingCooperationStudentCount: Int?,

        @Column(name = "OSOBOMIESIECY_P", nullable = true, length = 12, precision = 2)
        var arrivingPersonMonthCount: Double?,

        @Column(name = "LICZBA_DNI_P", nullable = true, length = 12, precision = 2)
        var arrivingDaysCount: Double?,

        @Column(name = "LICZBA_GODZIN_TYG_P", nullable = true, length = 12, precision = 2)
        var arrivingHoursPerWeek: Double?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "POZIOM_STUDIOW_W", referencedColumnName = "ID", nullable = true)
        var leavingStudiesLevel: ExchangeAttribute?,

        @Column(name = "LICZBA_STUDENTOW_W", nullable = true, length = 10)
        var leavingCooperationStudentCount: Int?,

        @Column(name = "OSOBOMIESIECY_W", nullable = true, length = 12, precision = 2)
        var leavingPersonMonthCount: Double?,

        @Column(name = "LICZBA_DNI_W", nullable = true, length = 12, precision = 2)
        var leavingDaysCount: Double?,

        @Column(name = "LICZBA_GODZIN_TYG_W", nullable = true, length = 12, precision = 2)
        var leavingHoursPerWeek: Double?,

        @Column(name = "WSZYSTKIE_DZIEDZINY", nullable = true, length = 1)
        var allFields: Char?,

        @Column(name = "UWAGI", nullable = true, length = 200)
        var comments: String?,

        @Column(name = "DATA_POCZ", nullable = false)
        var startDate: Date,

        @Column(name = "DATA_KON", nullable = false)
        var endDate: Date,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYP", nullable = false)
        var type: ExchangeAttribute,

        @Column(name = "KOD_SOKRATES", nullable = true, length = 5)
        var erasmusCode: String?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KOD_KRAJU_PRZYJMUJACEGO", referencedColumnName = "KOD", nullable = false)
        var hostCountryCode: Citizenship,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PRAC_ID_KU", referencedColumnName = "ID", nullable = true)
        var coordinatorEmployee: Employee?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OSZ_ID_KU", referencedColumnName = "ID", nullable = true)
        var coordinatorExternalPerson: ExternalPerson?,

        @OneToMany(mappedBy = "cooperation")
        val arrivals: MutableList<Arrival> = mutableListOf()
)
