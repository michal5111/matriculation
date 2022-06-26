package pl.poznan.ue.matriculation.cem.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.cem.domain.CemStudent
import pl.poznan.ue.matriculation.cem.repo.CemStudentRepository

@Service
@ConditionalOnProperty(
    value = ["pl.poznan.ue.matriculation.cem.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class CemStudentService(
    private val cemStudentRepository: CemStudentRepository
) {

    fun findById(id: Long): CemStudent? {
        return cemStudentRepository.findByIdOrNull(id)
    }
}
