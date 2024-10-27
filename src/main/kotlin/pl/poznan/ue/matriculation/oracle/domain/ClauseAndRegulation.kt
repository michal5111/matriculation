package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.time.LocalDate

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_KLAUZULE_I_REGULAMINY")
class ClauseAndRegulation(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_KL_REG_SEQ")
    @SequenceGenerator(sequenceName = "DZ_KL_REG_SEQ", allocationSize = 1, name = "DZ_KL_REG_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long,

    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "WERSJA", length = 2, nullable = false)
    val version: Long,

    @Column(name = "TYTUL", length = 200, nullable = false)
    val title: String,

    @Column(name = "TYP", length = 1, nullable = false)
    val type: String,

    @Column(name = "POCZATEK_OBOWIAZYWANIA", nullable = false)
    val startOfApplication: LocalDate,

    @Column(name = "KONIEC_OBOWIAZYWANIA", nullable = false)
    val endOfApplication: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
    val organizationalUnit: OrganizationalUnit,

    @Lob
    @Column(name = "TRESC", length = 2000, nullable = false)
    val content: String?,

    @Lob
    @Column(name = "TRESC_ANG", length = 2000, nullable = false)
    val contentEng: String?,

    @Column(name = "MOZNA_ODRZUCIC", length = 1, nullable = false)
    val canDeny: String,

    @Column(name = "OSOBA_PROGRAM", length = 1, nullable = false)
    val personOrProgramme: String,

    @Column(name = "TYTUL_ANG", length = 200, nullable = false)
    val titleEng: String,

    @OneToMany(mappedBy = "clauseAndRegulation", fetch = FetchType.LAZY)
    val clauseAndRegulationConfirmations: MutableList<ClauseAndRegulationConfirmation> = mutableListOf(),
) : BaseEntity()
