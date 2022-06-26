package pl.poznan.ue.matriculation.oracle.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.oracle.domain.Programme
import pl.poznan.ue.matriculation.oracle.repo.ProgrammeRepository

@Service
class ProgrammeService(
    private val programmeRepository: ProgrammeRepository
) {
    fun findAll(): List<Programme> {
        return programmeRepository.findAll()
    }

    fun findAll(sort: Sort): List<Programme> {
        return programmeRepository.findAll(sort)
    }

    fun findAllByCodeLike(pattern: String, sort: Sort): List<Programme> {
        return programmeRepository.findAllByCodeLike(pattern, sort)
    }
}
