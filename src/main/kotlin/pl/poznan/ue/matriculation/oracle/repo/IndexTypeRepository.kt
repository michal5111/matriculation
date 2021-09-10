package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.IndexType
import pl.poznan.ue.matriculation.oracle.dto.IndexTypeDto

@Repository
interface IndexTypeRepository : JpaRepository<IndexType, String> {

    @Query("SELECT new pl.poznan.ue.matriculation.oracle.dto.IndexTypeDto(it.code, it.description) FROM IndexType it WHERE it.isCurrent = true")
    fun getIndexTypeCodes(): List<IndexTypeDto>
}