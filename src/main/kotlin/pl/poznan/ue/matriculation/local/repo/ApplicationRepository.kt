package pl.poznan.ue.matriculation.local.repo

import org.hibernate.annotations.QueryHints.READ_ONLY
import org.hibernate.jpa.QueryHints.HINT_CACHEABLE
import org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.applications.Application
import java.util.stream.Stream
import javax.persistence.QueryHint

@Repository
interface ApplicationRepository : PagingAndSortingRepository<Application, Long> {

    @Query("SELECT a FROM Application a WHERE a.import.id = :importId")
    fun findAllByImportId(pageable: Pageable, @Param("importId") importId: Long): Page<Application>

    fun existsByForeignIdAndDatasourceId(foreignId: Long, datasourceId: String): Boolean

    fun getByForeignIdAndDatasourceId(foreignId: Long, foreignIdType: String): Application

    //@QueryHints(value = [QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE)]) //MySql
    @QueryHints(value = [
        QueryHint(name = HINT_FETCH_SIZE, value = "50"),
        QueryHint(name = HINT_CACHEABLE, value = "false"),
        QueryHint(name = READ_ONLY, value = "true")
    ])
    @Query("select an from Application an where an.import.id = :id and (an.importStatus = 'NOT_IMPORTED' or an.importStatus = 'ERROR')")
    fun getAllByImportIdAndApplicationImportStatus(@Param("id") importId: Long): Stream<Application>

//    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun deleteAllByImportId(importId: Long)

    fun findAllByImportId(importId: Long): Stream<Application>
}