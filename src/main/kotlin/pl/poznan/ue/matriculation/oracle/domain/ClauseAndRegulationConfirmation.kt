package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "DZ_KL_REG_POTWIERDZENIA")
class ClauseAndRegulationConfirmation(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_KL_REG_POTW_SEQ")
    @SequenceGenerator(sequenceName = "DZ_KL_REG_POTW_SEQ", allocationSize = 1, name = "DZ_KL_REG_POTW_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID")
    val person: Person,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KL_REG_ID", referencedColumnName = "ID")
    val clauseAndRegulation: ClauseAndRegulation,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_PODJECIA_DECYZJI")
    val confirmationDate: LocalDateTime? = null,

    @Column(name = "DECYZJA", length = 1, nullable = false)
    val decision: String = "X",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID")
    val personProgramme: PersonProgramme,

    @Temporal(TemporalType.DATE)
    @Column(name = "TERMIN_PODJECIA_DECYZJI")
    val decisionDeadline: LocalDate?,

    @Temporal(TemporalType.DATE)
    @Column(name = "ZAKONCZENIE_OBOWIAZYWANIA")
    val terminationDate: LocalDate?,

    @Column(name = "DATA_OD", nullable = false)
    val dateFrom: LocalDate? = LocalDate.now(),
) : BaseEntity()
