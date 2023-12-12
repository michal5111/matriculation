package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.*
import javax.persistence.*

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

    @Column(name = "DATA_PODJECIA_DECYZJI")
    val confirmationDate: Date? = null,

    @Column(name = "DECYZJA", length = 1, nullable = false)
    val decision: String = "X",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID")
    val personProgramme: PersonProgramme,

    @Column(name = "TERMIN_PODJECIA_DECYZJI")
    val decisionDeadline: Date,

    @Column(name = "ZAKONCZENIE_OBOWIAZYWANIA")
    val terminationDate: Date?,

    @Column(name = "DATA_OD", nullable = false)
    val dateFrom: Date? = Date(),
) : BaseEntity()
