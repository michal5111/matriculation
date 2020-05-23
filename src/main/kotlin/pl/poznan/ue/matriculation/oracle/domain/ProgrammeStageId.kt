package pl.poznan.ue.matriculation.oracle.domain

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class ProgrammeStageId(
        @Column(name = "PRG_KOD")
        val programmeId: String,

        @Column(name = "ETP_KOD")
        val stageId: String
) : Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ProgrammeStageId

                if (programmeId != other.programmeId) return false
                if (stageId != other.stageId) return false

                return true
        }

        override fun hashCode(): Int {
                var result = programmeId.hashCode()
                result = 31 * result + stageId.hashCode()
                return result
        }
}