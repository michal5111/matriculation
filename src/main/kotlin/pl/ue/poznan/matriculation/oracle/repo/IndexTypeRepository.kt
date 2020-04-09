package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.oracle.domain.IndexType
import pl.ue.poznan.matriculation.oracle.dto.IndexTypeDto

@Repository
interface IndexTypeRepository: JpaRepository<IndexType, String> {

    @Query("SELECT new pl.ue.poznan.matriculation.oracle.dto.IndexTypeDto(it.code, it.description) FROM IndexType it")
    fun getIndexTypeCodes(): List<IndexTypeDto>
}