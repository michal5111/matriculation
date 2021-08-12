package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_ETAPY_OSOB")
class PersonStage(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_ETP_ST_SEQ")
    @SequenceGenerator(sequenceName = "DZ_ETP_ST_SEQ", allocationSize = 1, name = "DZ_ETP_ST_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Column(name = "DATA_ZAKON", nullable = false)
    var endDate: Date,

    @Column(name = "STATUS_ZALICZENIA", length = 1, nullable = false)
    var passStatus: Char,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(name = "ETP_KOD", referencedColumnName = "ETP_KOD"),
        JoinColumn(name = "PRG_KOD", referencedColumnName = "PRG_KOD")
    )
    var programmeStage: ProgrammeStage,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID")
    var personProgramme: PersonProgramme,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CDYD_KOD", referencedColumnName = "KOD")
    var didacticCycle: DidacticCycle,

    @Column(name = "STATUS_ZAL_KOMENTARZ", length = 1000, nullable = true)
    var passStatusComment: String? = null,

    @Column(name = "LICZBA_WAR", length = 10, nullable = true)
    var conditionCount: Int? = 1,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WYM_CDYD_KOD", referencedColumnName = "KOD")
    var didacticCycleRequirement: DidacticCycle,

    @Column(name = "CZY_PLATNY_NA_2_KIER", length = 1, nullable = true)
    var isPayable: Char? = null,

    @Column(name = "KOLEJNOSC", length = 10, nullable = false)
    var order: Int
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonStage

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}