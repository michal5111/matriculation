package pl.poznan.ue.matriculation.cem.repo

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.cem.domain.CemStudent

@Repository
interface CemStudentRepository : PagingAndSortingRepository<CemStudent, Long>
