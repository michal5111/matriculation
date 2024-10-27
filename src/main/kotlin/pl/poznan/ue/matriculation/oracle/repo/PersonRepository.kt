package pl.poznan.ue.matriculation.oracle.repo

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.entityRepresentations.PersonBasicData
import java.time.LocalDate

@Repository
interface PersonRepository : JpaRepository<Person, Long> {

    fun findAll(specification: Specification<Person>): Person?

    @Lock(LockModeType.OPTIMISTIC)
    @Query(
        """
        select p
        from Person p
        where p.id = :personId
        or p.pesel = :pesel
        or UPPER(FUNCTION('REGEXP_REPLACE',p.idNumber,'[^a-zA-Z0-9]+','')) in (:idNumbers)
        or p.id = (select distinct h.person.id from PersonChangeHistory h where UPPER(FUNCTION('REGEXP_REPLACE',h.idNumber,'[^a-zA-Z0-9]+','')) in (:idNumbers))
        or p.id = (select distinct od.person.id from OwnedDocument od where UPPER(FUNCTION('REGEXP_REPLACE',od.number,'[^a-zA-Z0-9]+','')) in (:idNumbers))
    """
    )
    fun findOneByPeselOrIdNumberOrPersonId(
        personId: Long?,
        pesel: String,
        idNumbers: List<String>
    ): Person?

    @Query(
        """
        select p
        from Person p
        where trim(p.name) = trim(:name)
        and trim(p.surname) = trim(:surname)
        and p.pesel is null
        and p.birthDate = :birthDate
        and (p.idNumber is null or UPPER(FUNCTION('REGEXP_REPLACE',p.idNumber,'[^a-zA-Z0-9]+','')) not in (:idNumbers))
    """
    )
    fun findPotentialDuplicate(
        name: String,
        surname: String,
        birthDate: LocalDate,
        idNumbers: List<String>
    ): List<PersonBasicData>

    @Query(
        """
        select p
        from Person p
        where trim(p.name) = trim(:name)
        and trim(p.surname) = trim(:surname)
        and p.birthDate = :birthDate
        and (p.idNumber is null or UPPER(FUNCTION('REGEXP_REPLACE',p.idNumber,'[^a-zA-Z0-9]+','')) not in (:idNumbers))
    """
    )
    fun findPotentialDuplicateWithNullPesel(
        name: String,
        surname: String,
        birthDate: LocalDate,
        idNumbers: List<String>
    ): List<PersonBasicData>
}
