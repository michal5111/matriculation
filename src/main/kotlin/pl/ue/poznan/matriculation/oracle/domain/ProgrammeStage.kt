package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_ETAPY_PROGRAMOW")
data class ProgrammeStage(

        @EmbeddedId
        val programmeStageId: ProgrammeStageId,

        @ManyToOne(fetch = FetchType.LAZY)
        @MapsId("PRG_KOD")
        @JoinColumn(name = "PRG_KOD")
        val programme: Programme,

        @ManyToOne(fetch = FetchType.LAZY)
        @MapsId("ETP_KOD")
        @JoinColumn(name = "ETP_KOD")
        val stage: Stage,

        @Column(name = "NR_ROKU", length = 2, nullable = true)
        var yearNumber: Int? = null,

        @Column(name = "PIERWSZY_ETAP", length = 1, nullable = false)
        var firstStage: Char = 'N',

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TCDYD_KOD", referencedColumnName = "KOD", nullable = false)
        var didacticCycleType: DidacticCycleType,

        @Column(name = "LICZBA_WAR", length = 10, nullable = true)
        var conditionCount: Int? = 1,

        @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
        var display: Char = 'T',

        @OneToMany(mappedBy = "programmeStage", fetch = FetchType.LAZY)
        var personStages: MutableList<PersonStage>
)