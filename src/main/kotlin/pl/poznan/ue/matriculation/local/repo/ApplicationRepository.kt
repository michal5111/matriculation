package pl.poznan.ue.matriculation.local.repo

import org.hibernate.annotations.QueryHints.READ_ONLY
import org.hibernate.jpa.QueryHints.HINT_CACHEABLE
import org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import java.util.stream.Stream
import javax.persistence.QueryHint

@Repository
interface ApplicationRepository : PagingAndSortingRepository<Application, Long> {

    @Query("SELECT a FROM Application a WHERE a.import.id = :importId")
    fun findAllByImportId(pageable: Pageable, @Param("importId") importId: Long): Page<Application>

    @QueryHints(
        value = [
            QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            QueryHint(name = HINT_CACHEABLE, value = "false"),
            QueryHint(name = READ_ONLY, value = "true")
        ]
    )
    @Query("SELECT a FROM Application a WHERE a.import.id = :importId")
    fun findAllByImportIdStream(@Param("importId") importId: Long): Stream<Application>

    fun findByForeignIdAndDataSourceId(foreignId: Long, foreignIdType: String): Application?

    @EntityGraph("application.applicant")
    //@QueryHints(value = [QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE)]) //MySql
    @QueryHints(
        value = [
            QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            QueryHint(name = HINT_CACHEABLE, value = "false"),
            QueryHint(name = READ_ONLY, value = "true")
        ]
    )
    fun getAllByImportIdAndImportStatusIn(
        importId: Long,
        importStatuses: List<ApplicationImportStatus>,
        sort: Sort
    ): Stream<Application>

    fun deleteAllByImportId(importId: Long)

    fun findAllByImportId(importId: Long): Stream<Application>

    @QueryHints(
        value = [
            QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            QueryHint(name = HINT_CACHEABLE, value = "false"),
            QueryHint(name = READ_ONLY, value = "true")
        ]
    )
    @Query(
        """
        SELECT a
        FROM Application a
        WHERE a.import.id = :importId
        AND a.applicant.potentialDuplicateStatus in (:statuses)
    """
    )
    fun findAllByImportIdStreamAndApplicantPotentialDuplicateStatus(
        @Param("importId") importId: Long,
        @Param("statuses") statuses: List<DuplicateStatus>
    ): Stream<Application>
}
