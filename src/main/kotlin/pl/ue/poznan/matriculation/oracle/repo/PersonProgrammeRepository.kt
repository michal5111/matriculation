package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.oracle.domain.Person
import pl.ue.poznan.matriculation.oracle.domain.PersonProgramme
import java.util.*

@Repository
interface PersonProgrammeRepository: JpaRepository<PersonProgramme, Long> {

    @Query("SELECT MAX(pp.plannedDateOfCompletion) FROM PersonProgramme pp WHERE pp.person = :person")
    fun getPreviousStudyEndDate(person: Person): Date?
}