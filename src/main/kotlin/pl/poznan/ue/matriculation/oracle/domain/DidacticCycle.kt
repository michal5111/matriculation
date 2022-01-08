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
@Table(name = "DZ_CYKLE_DYDAKTYCZNE")
class DidacticCycle(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = false)
    val description: String,

    @Column(name = "DATA_OD", nullable = false)
    val dateFrom: Date,

    @Column(name = "DATA_DO", nullable = false)
    val dateTo: Date,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
    val display: Boolean = false,

    @Column(name = "DATA_ZAKON", nullable = false)
    val endDate: Date,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TCDYD_KOD", referencedColumnName = "KOD")
    val didacticCycleType: DidacticCycleType,

    @Column(name = "STATUS_CYKLU", length = 1, nullable = false)
    val cycleStatus: Char = 'A',

    @Column(name = "DESCRIPTION", length = 100, nullable = false)
    val descriptionEng: String? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "ARCHIWIZACJA_ZALICZEN", length = 1, nullable = false)
    val passArchive: Boolean = false,

    @Column(name = "DATA_MOD_ARCH_ZAL", nullable = false)
    val passArchiveModDate: Date,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "ARCHIWIZACJA_ETAPOW", length = 1, nullable = false)
    val stageArchive: Boolean = false,

    @Column(name = "DATA_MOD_ARCH_ETP", nullable = false)
    val stageArchiveModDate: Date,

    @Column(name = "KOLEJNOSC", length = 10, nullable = false)
    val order: Int = 0,

    @OneToMany(mappedBy = "didacticCycle", fetch = FetchType.LAZY)
    val personStages: MutableList<PersonStage> = mutableListOf(),

    @OneToMany(mappedBy = "didacticCycleRequirement", fetch = FetchType.LAZY)
    val personStagesRequirements: MutableList<PersonStage> = mutableListOf(),

    @OneToMany(mappedBy = "didacticCycleAcademicYear", fetch = FetchType.LAZY)
    val arrivals: MutableList<Arrival> = mutableListOf(),

    @OneToMany(mappedBy = "financingDidacticCycleAcademicYear", fetch = FetchType.LAZY)
    val financingArrivals: List<Arrival>,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DidacticCycle

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
