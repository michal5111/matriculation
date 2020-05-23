package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.PersonProgramme
import pl.poznan.ue.matriculation.oracle.domain.Programme
import pl.poznan.ue.matriculation.oracle.domain.Student
import java.util.*

@Repository
interface PersonProgrammeRepository: JpaRepository<PersonProgramme, Long> {

    @Query("SELECT MAX(pp.plannedDateOfCompletion) FROM PersonProgramme pp WHERE pp.person = :person")
    fun getPreviousStudyEndDate(person: Person): Date?

    @Query("SELECT pp FROM PersonProgramme pp WHERE pp.isDefault = 'T' AND pp.person.id = :id")
    fun getDefaultProgramme(@Param("id") personId: Long): PersonProgramme?

    fun findByPersonAndProgrammeAndStudent(person: Person, programme: Programme, student: Student): PersonProgramme?
}