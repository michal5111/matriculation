package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.oracle.domain.PersonProgramme
import java.time.LocalDate

@Repository
interface PersonProgrammeRepository : JpaRepository<PersonProgramme, Long> {

    @Transactional
    @Modifying
    @Query(
        """
        UPDATE PersonProgramme
        set isDefault = false
        where isDefault = true
        and (
            plannedDateOfCompletion is null
            or plannedDateOfCompletion <= :dateOfAddmision
            or status <> 'STU'
        ) and person.id = :personId
    """
    )
    fun updateToNotDefault(personId: Long, dateOfAddmision: LocalDate): Int
}
