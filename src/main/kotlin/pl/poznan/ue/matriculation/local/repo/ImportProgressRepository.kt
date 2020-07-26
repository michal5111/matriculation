package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.ImportProgress

@Repository
interface ImportProgressRepository : JpaRepository<ImportProgress, Long> {

    @Modifying
    @Transactional(transactionManager = "transactionManager")
    @Query("update ImportProgress ip set ip.error = :error where ip.id = :importId")
    fun setError(error: String, importId: Long)

    @Modifying
    @Transactional(transactionManager = "transactionManager")
    @Query("update ImportProgress ip set ip.importStatus = :importStatus where ip.id = :importId")
    fun setStatus(importStatus: ImportStatus, importId: Long)

//    @Query("select new pl.poznan.ue.matriculation.local.dto.ImportProgressDto(ip.id, ip.importedApplications, ip.saveErrors, ip.savedApplicants, ip.totalCount, ip.error) from ImportProgress ip where ip.id = :importId")
//    fun getImportProgressDtoById(importId: Long): ImportProgressDto?
}