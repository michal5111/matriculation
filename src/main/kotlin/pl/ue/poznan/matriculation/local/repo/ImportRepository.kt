package pl.ue.poznan.matriculation.local.repo

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.local.domain.import.Import

@Repository
interface ImportRepository: PagingAndSortingRepository<Import, Long> {

    fun existsByProgrammeCodeAndRegistration(programmeCode: String, registration: String): Boolean
}