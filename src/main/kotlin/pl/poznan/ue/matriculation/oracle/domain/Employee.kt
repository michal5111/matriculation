package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_PRACOWNICY")
class Employee(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_OS_SEQ")
    @SequenceGenerator(sequenceName = "DZ_OS_SEQ", allocationSize = 1, name = "DZ_OS_SEQ")
    @Column(name = "ID", nullable = false, updatable = false, length = 10)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = true)
    var person: Person? = null,

    @Column(name = "PIERWSZE_ZATR", nullable = false, length = 1)
    val isFirstEmployment: Char,

    @Column(name = "NUMER_AKT", nullable = true, length = 20)
    val fileNumber: String?,

    @Column(name = "NR_KART", nullable = true, length = 100)
    val cardNumber: String?,

    @Column(name = "TELEFON1", nullable = true, length = 30)
    val phoneNumber: String?,

    @Column(name = "TELEFON2", nullable = true, length = 30)
    val phoneNumber2: String?,

    @Temporal(TemporalType.DATE)
    @Column(name = "BADANIA_OKRESOWE", nullable = true)
    val periodicExaminations: Date?,

    @Column(name = "KONS_DO_ZMIANY", nullable = true, length = 1)
    val shouldUpdateConsultationDate: Char?,

    @Column(name = "KONSULTACJE", nullable = true, length = 1000)
    val consultation: String?,

    @Column(name = "ZAINTERESOWANIA", nullable = true, length = 1000)
    val interests: String?,

    @Column(name = "ZAINTERESOWANIA_ANG", nullable = true, length = 1000)
    val interestsEng: String?,

    @Temporal(TemporalType.DATE)
    @Column(name = "EMERYTURA_DATA", nullable = true)
    val retirementDate: Date?,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_NADANIA_TYTULU", nullable = true)
    val dateOfConferringTheTitle: Date?,

    @Column(name = "AKTYWNY", nullable = false, length = 1)
    val active: Char,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_PRZESWIETLENIA", nullable = true)
    val XRayDate: Date?,

    @OneToMany(
        mappedBy = "coordinatorEmployee",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var employeeContracts: MutableList<Contract> = mutableListOf(),

    @OneToMany(
        mappedBy = "coordinatorEmployee",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var EmployeeCooperations: MutableList<Cooperation> = mutableListOf(),

    @OneToMany(
        mappedBy = "organizationalSupervisorEmployee",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var EmployeeOrganizationalSupervisorArrivals: MutableList<Arrival> = mutableListOf(),

    @OneToMany(
        mappedBy = "researchTutorEmployee",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var EmployeeResearchTutorArrivals: MutableList<Arrival> = mutableListOf(),
) : BaseEntity()
