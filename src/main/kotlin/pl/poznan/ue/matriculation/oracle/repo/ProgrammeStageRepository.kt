package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.ProgrammeStage
import pl.poznan.ue.matriculation.oracle.domain.ProgrammeStageId

@Repository
interface ProgrammeStageRepository: JpaRepository<ProgrammeStage, ProgrammeStageId> {

    @Query("SELECT ps.programmeStageId.stageId FROM ProgrammeStage ps WHERE ps.programmeStageId.programmeId = :programmeCode")
    fun getAllStageCodesByProgrammeCode(@Param("programmeCode") programmeCode: String): List<String>
}