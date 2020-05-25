package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_TYPY_CYKLI_DYDAKTYCZNYCH")
class DidacticCycleType(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 100, nullable = false)
        var description: String,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "didacticCycleType")
        var programmes: MutableList<Programme> = mutableListOf(),

        @OneToMany(mappedBy = "didacticCycleType", fetch = FetchType.LAZY)
        val programmeStages: MutableList<ProgrammeStage> = mutableListOf(),

        @OneToMany(mappedBy = "didacticCycleType", fetch = FetchType.LAZY)
        val didacticCycles: MutableList<DidacticCycle> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DidacticCycleType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}