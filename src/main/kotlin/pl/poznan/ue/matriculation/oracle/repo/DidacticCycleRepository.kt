package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.DidacticCycle
import java.util.*

@Repository
interface DidacticCycleRepository : JpaRepository<DidacticCycle, String>, DidacticCycleRepositoryCustom {

    @Query("select d from DidacticCycle d where (d.dateFrom = :dateFrom or d.endDate = :dateTo) and d.didacticCycleType = 'ROK'")
    fun findDidacticCycleYearBySemesterDates(
        @Param("dateFrom") dateFrom: Date,
        @Param("dateTo") dateTo: Date
    ): DidacticCycle?

    @Query(
        nativeQuery = true,
        value = "select DATA_DO from DZ_CYKLE_DYDAKTYCZNE where KOD = (select NAST_CYKL_KOD FROM DZ_KOLEJNOSC_CYKLI_DYD where POPRZ_CYKL_KOD = :didacticCycleCode)"
    )
    fun getNextDidacticCycleEndDate(@Param("didacticCycleCode") didacticCycleCode: String): Date?
}
