package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ImportDtoJpa
import java.util.*

@Repository
interface ImportRepository : JpaRepository<Import, Long>, PagingAndSortingRepository<Import, Long> {

    @Query(
        """
        select
            new pl.poznan.ue.matriculation.local.dto.ImportDtoJpa(
                i.programmeCode,
                i.programmeForeignId,
                i.registration,
                i.indexPoolCode,
                i.startDate,
                i.dateOfAddmision,
                i.stageCode,
                i.didacticCycleCode,
                i.dataSourceId
            ) from Import i
            where i.id = :id
    """
    )
    fun getDtoById(@Param("id") importId: Long): ImportDtoJpa

    @EntityGraph("import.importProgress")
    override fun findById(id: Long): Optional<Import>

    fun existsByProgrammeForeignIdAndRegistrationAndStageCodeAndDidacticCycleCode(
        programmeForeignId: String,
        registration: String,
        stageCode: String,
        didacticCycleCode: String,
    ): Boolean

}
