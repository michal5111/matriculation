package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.oracle.domain.PersonProgramme

@Repository
interface PersonProgrammeRepository : JpaRepository<PersonProgramme, Long> {

    @Transactional
    @Query("SELECT pp FROM PersonProgramme pp WHERE pp.isDefault = 'T' AND pp.person.id = :id")
    fun getDefaultProgramme(@Param("id") personId: Long?): PersonProgramme?

    @Transactional
    @Modifying
    @Query("UPDATE PersonProgramme set isDefault = 'N' where id = :id")
    fun updateToNotDefault(@Param("id") personProgrammeId: Long?)
}