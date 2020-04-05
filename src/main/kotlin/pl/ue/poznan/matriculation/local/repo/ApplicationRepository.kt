package pl.ue.poznan.matriculation.local.repo

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.local.domain.applications.Application

@Repository
interface ApplicationRepository: PagingAndSortingRepository<Application, Long> {

    @Query("SELECT a FROM Application a WHERE a.import.id = :importId")
    fun findAllByImportId(pageable: Pageable, @Param("importId") importId: Long): Page<Application>

    fun existsByIrkId(irkId: Long): Boolean

    fun getByIrkId(irkId: Long): Application
}