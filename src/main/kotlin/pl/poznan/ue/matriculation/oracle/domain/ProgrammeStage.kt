package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import javax.persistence.*

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_ETAPY_PROGRAMOW")
class ProgrammeStage(

    @EmbeddedId
    val programmeStageId: ProgrammeStageId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("programmeId")
    @JoinColumn(name = "PRG_KOD")
    val programme: Programme,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("stageId")
    @JoinColumn(name = "ETP_KOD")
    val stage: Stage,

    @Column(name = "NR_ROKU", length = 2, nullable = true)
    val yearNumber: Int? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "PIERWSZY_ETAP", length = 1, nullable = false)
    val firstStage: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TCDYD_KOD", referencedColumnName = "KOD", nullable = false)
    val didacticCycleType: DidacticCycleType,

    @Column(name = "LICZBA_WAR", length = 10, nullable = true)
    val conditionCount: Int? = 1,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
    val display: Boolean = true,

    @OneToMany(mappedBy = "programmeStage", fetch = FetchType.LAZY)
    var personStages: MutableList<PersonStage>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProgrammeStage

        if (programmeStageId != other.programmeStageId) return false

        return true
    }

    override fun hashCode(): Int {
        return programmeStageId.hashCode()
    }
}
