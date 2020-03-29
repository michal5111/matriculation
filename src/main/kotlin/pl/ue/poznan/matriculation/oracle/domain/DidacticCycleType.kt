package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_TYPY_CYKLI_DYDAKTYCZNYCH")
data class DidacticCycleType(
        @Id
        @NotBlank
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
)