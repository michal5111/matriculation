package pl.ue.poznan.matriculation.oracle.domain

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class ProgrammeStageId(
        @Column(name = "PRG_KOD")
        val programmeId: String,

        @Column(name = "ETP_KOD")
        val stageId: String
): Serializable