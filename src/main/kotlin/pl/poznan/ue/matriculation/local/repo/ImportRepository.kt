package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.import.Import

@Repository
interface ImportRepository : JpaRepository<Import, Long>, PagingAndSortingRepository<Import, Long> {

    fun existsByProgrammeCodeAndRegistration(programmeCode: String, registration: String): Boolean
}