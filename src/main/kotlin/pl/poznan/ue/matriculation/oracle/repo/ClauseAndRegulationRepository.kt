package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pl.poznan.ue.matriculation.oracle.domain.ClauseAndRegulation

interface ClauseAndRegulationRepository : JpaRepository<ClauseAndRegulation, Long> {
    @Query(
        """
        select c
        from ClauseAndRegulation c
        where c.code = :code
        and c.version = (
            select max(c2.version)
            from ClauseAndRegulation c2
            where c2.code = :code
        )
    """
    )
    fun findLatestByCode(code: String): ClauseAndRegulation?
}
