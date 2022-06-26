package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Programme

@Repository
interface ProgrammeRepository : JpaRepository<Programme, String> {
    fun findAllByCodeLike(pattern: String, sort: Sort): List<Programme>
}
