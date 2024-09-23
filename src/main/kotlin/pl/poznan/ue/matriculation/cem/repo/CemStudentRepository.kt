package pl.poznan.ue.matriculation.cem.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.cem.domain.CemStudent

@Repository
interface CemStudentRepository : JpaRepository<CemStudent, Long>
