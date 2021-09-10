package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant

@Repository
interface ApplicantRepository : JpaRepository<Applicant, Long> {

    @EntityGraph("applicant.data")
    fun findByForeignIdAndDataSourceId(foreignId: Long, dataSourceId: String): Applicant?

    fun findByUsosId(usosId: Long): Applicant?

    @Modifying
    @Query("delete from Applicant a where a.id in (select ap.applicant.id from Application ap where ap.import.id = :importId) and a.id not in (select ap2.applicant.id from Application ap2 where ap2.import.id <> :importId)")
    fun deleteAllByImportId(importId: Long)
}
