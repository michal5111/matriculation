package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_CYKLE_DYDAKTYCZNE")
class DidacticCycle(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 100, nullable = false)
        var description: String,

        @Column(name = "DATA_OD", nullable = false)
        var dateFrom: Date,

        @Column(name = "DATA_DO", nullable = false)
        var dateTo: Date,

        @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
        var display: Char = 'N',

        @Column(name = "DATA_ZAKON", nullable = false)
        var endDate: Date,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TCDYD_KOD", referencedColumnName = "KOD")
        var didacticCycleType: DidacticCycleType,

        @Column(name = "STATUS_CYKLU", length = 1, nullable = false)
        var cycleStatus: Char = 'A',

        @Column(name = "DESCRIPTION", length = 100, nullable = false)
        var descriptionEng: String? = null,

        @Column(name = "ARCHIWIZACJA_ZALICZEN", length = 1, nullable = false)
        var passArchive: Char = 'N',

        @Column(name = "DATA_MOD_ARCH_ZAL", nullable = false)
        var passArchiveModDate: Date,

        @Column(name = "ARCHIWIZACJA_ETAPOW", length = 1, nullable = false)
        var stageArchive: Char = 'N',

        @Column(name = "DATA_MOD_ARCH_ETP", nullable = false)
        var stageArchiveModDate: Date,

        @Column(name = "KOLEJNOSC", length = 10, nullable = false)
        var order: Int = 0,

        @OneToMany(mappedBy = "didacticCycle", fetch = FetchType.LAZY)
        val personStages: MutableList<PersonStage> = mutableListOf(),

        @OneToMany(mappedBy = "didacticCycleRequirement", fetch = FetchType.LAZY)
        var personStagesRequirements: MutableList<PersonStage> = mutableListOf()
) {
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