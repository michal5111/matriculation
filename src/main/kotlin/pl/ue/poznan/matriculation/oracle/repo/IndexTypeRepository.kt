package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.oracle.domain.IndexType

@Repository
interface IndexTypeRepository: JpaRepository<IndexType, String> {

    @Query("SELECT it.code FROM IndexType it")
    fun getIndexTypeCodes(): List<String>
}