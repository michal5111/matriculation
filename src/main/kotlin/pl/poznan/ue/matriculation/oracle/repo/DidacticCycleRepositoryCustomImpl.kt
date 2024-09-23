package pl.poznan.ue.matriculation.oracle.repo

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

class DidacticCycleRepositoryCustomImpl : DidacticCycleRepositoryCustom {

    @PersistenceContext(unitName = "oracle")
    private lateinit var entityManager: EntityManager

    override fun findDidacticCycleCodes(didacticCycleCode: String, maxResults: Int): List<String> {
        val query = entityManager
            .createQuery(
                """
                SELECT
                    dc.code
                FROM DidacticCycle dc
                WHERE dc.code LIKE :didacticCycleCode
                ORDER BY dc.dateTo DESC
            """.trimIndent(), String::class.java
            )
        return query!!.setParameter("didacticCycleCode", "${didacticCycleCode}%").setMaxResults(maxResults).resultList
    }
}
