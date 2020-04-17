package pl.ue.poznan.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.local.domain.import.ImportProgress
import pl.ue.poznan.matriculation.local.domain.import.ImportProgressDto

@Repository
interface ImportProgressRepository: JpaRepository<ImportProgress, Long> {

    @Query("select new pl.ue.poznan.matriculation.local.domain.import.ImportProgressDto(ip.id, ip.importedApplications, ip.saveErrors, ip.savedApplicants, ip.totalCount, ip.importStatus, ip.error) from ImportProgress ip where ip.id = :id")
    fun getProgress(@Param("id") importId: Long): ImportProgressDto?
}