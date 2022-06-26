package pl.poznan.ue.matriculation.cem.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.cem.domain.CemApplication
import pl.poznan.ue.matriculation.cem.enum.ApplicationStatus
import pl.poznan.ue.matriculation.cem.repo.CemApplicationRepository

@Service
@ConditionalOnProperty(
    value = ["pl.poznan.ue.matriculation.cem.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class CemApplicationService(
    private val cemApplicationRepository: CemApplicationRepository
) {

    fun findById(id: Long): CemApplication? {
        return cemApplicationRepository.findByIdOrNull(id)
    }

    fun findAllByCourseEditionIdAndStatus(
        pageable: Pageable,
        courseEditionId: Long,
        status: ApplicationStatus
    ): Page<CemApplication> {
        return cemApplicationRepository.findAllByCourseEditionIdAndStatus(
            pageable,
            courseEditionId,
            status
        )
    }
}
