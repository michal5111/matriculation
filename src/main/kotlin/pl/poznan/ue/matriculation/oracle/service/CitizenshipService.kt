package pl.poznan.ue.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.oracle.domain.Citizenship
import pl.poznan.ue.matriculation.oracle.repo.CitizenshipRepository

@Service
class CitizenshipService(
    private val citizenshipRepository: CitizenshipRepository
) {
    fun findByAnyName(name: String): Citizenship? {
        return citizenshipRepository.findByAnyName(name)
    }
}
