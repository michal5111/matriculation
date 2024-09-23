package pl.poznan.ue.matriculation.oracle.repo

import jakarta.persistence.TemporalType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Temporal
import pl.poznan.ue.matriculation.oracle.domain.PersonChangeHistory
import java.util.*

interface PersonChangeHistoryRepository : JpaRepository<PersonChangeHistory, Long> {
    fun findByPersonIdAndChangeDate(
        personId: Long?,
        @Temporal(TemporalType.DATE) changeDate: Date
    ): PersonChangeHistory?
}
