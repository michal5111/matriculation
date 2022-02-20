package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import

@Repository
interface ImportRepository : JpaRepository<Import, Long>, PagingAndSortingRepository<Import, Long> {

    @EntityGraph(attributePaths = ["error", "importStatus"])
    fun getErrorAndStatusById(id: Long): Import

    @EntityGraph(attributePaths = ["error"])
    fun getWithErrorById(id: Long): Import

    @EntityGraph(attributePaths = ["ImportStatus"])
    fun getWithImportStatusById(id: Long): Import

    fun existsByProgrammeForeignIdAndRegistrationAndStageCodeAndDidacticCycleCode(
        programmeForeignId: String,
        registration: String,
        stageCode: String,
        didacticCycleCode: String,
    ): Boolean

    @Modifying
    @Transactional(transactionManager = "transactionManager")
    @Query("update Import ip set ip.error = :error where ip.id = :importId")
    fun setError(error: String, importId: Long)

    @Modifying
    @Transactional(transactionManager = "transactionManager")
    @Query("update Import ip set ip.importStatus = :importStatus where ip.id = :importId")
    fun setStatus(importStatus: ImportStatus, importId: Long)
}
