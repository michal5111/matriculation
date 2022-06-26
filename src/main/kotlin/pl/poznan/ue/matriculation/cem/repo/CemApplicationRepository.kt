package pl.poznan.ue.matriculation.cem.repo

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.cem.domain.CemApplication
import pl.poznan.ue.matriculation.cem.enum.ApplicationStatus

@Repository
interface CemApplicationRepository : PagingAndSortingRepository<CemApplication, Long> {

    fun findAllByCourseEditionIdAndStatus(
        pageable: Pageable,
        courseEditionId: Long,
        status: ApplicationStatus
    ): Page<CemApplication>
}
