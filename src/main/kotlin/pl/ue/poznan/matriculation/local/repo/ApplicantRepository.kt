package pl.ue.poznan.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant

@Repository
interface ApplicantRepository: JpaRepository<Applicant, Long> {
    fun existsByIrkId(irkId: Long): Boolean

    fun findByIrkId(irkId: Long): Applicant?
}