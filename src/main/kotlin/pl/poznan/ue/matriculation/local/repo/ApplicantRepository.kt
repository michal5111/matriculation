package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant

@Repository
interface ApplicantRepository : JpaRepository<Applicant, Long> {

    fun findByForeignIdAndDatasourceId(foreignId: Long, datasourceId: String): Applicant?

    fun findByUsosId(usosId: Long): Applicant?

//    @Modifying
//    @Transactional
//    @Query("delete from Applicant a where ")
//    fun deleteAllByApplicationsId()
}