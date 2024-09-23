package pl.poznan.ue.matriculation.cem.repo

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.cem.domain.CemApplication
import pl.poznan.ue.matriculation.cem.enum.ApplicationStatus

@Repository
interface CemApplicationRepository : JpaRepository<CemApplication, Long> {

    fun findAllByCourseEditionIdAndStatus(
        pageable: Pageable,
        courseEditionId: Long,
        status: ApplicationStatus
    ): Page<CemApplication>
}
