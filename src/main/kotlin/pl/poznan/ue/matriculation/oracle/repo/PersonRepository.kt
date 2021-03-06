package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.entityRepresentations.PersonBasicData
import java.util.*
import javax.persistence.LockModeType

@Repository
interface PersonRepository : JpaRepository<Person, Long> {

    fun findAll(specification: Specification<Person>): Person?

    @EntityGraph("person.basicDataAndAddresses")
    @Lock(LockModeType.OPTIMISTIC)
    fun findByPesel(pesel: String): Person?

    @EntityGraph("person.basicDataAndAddresses")
    @Lock(LockModeType.OPTIMISTIC)
    @Query(
        """
        select p
        from Person p
        where UPPER(p.idNumber) in (:idNumbers)
        or p.id = (select h.person.id from PersonChangeHistory h where UPPER(h.idNumber) in (:idNumbers))
    """
    )
    fun findByIdNumberIn(idNumbers: List<String>): Person?

    @EntityGraph("person.basicDataAndAddresses")
    @Lock(LockModeType.OPTIMISTIC)
    @Query(
        """
        select p
        from Person p
        where UPPER(FUNCTION('REPLACE',p.email,' ','')) = UPPER(FUNCTION('REPLACE',:email,' ',''))
    """
    )
    fun findOneByEmail(email: String): Person?

    @EntityGraph("person.basicDataAndAddresses")
    @Lock(LockModeType.OPTIMISTIC)
    @Query(
        """
        select p
        from Person p
        where UPPER(FUNCTION('REPLACE',p.privateEmail,' ','')) = UPPER(FUNCTION('REPLACE',:privateEmail,' ',''))
    """
    )
    fun findOneByPrivateEmail(privateEmail: String): Person?

    @Lock(LockModeType.OPTIMISTIC)
    @Query(
        """
        select p
        from Person p
        where p.id = :personId
        or p.pesel = :pesel
        or UPPER(FUNCTION('REGEXP_REPLACE',p.idNumber,'[^a-zA-Z0-9]+','')) in (:idNumbers)
        or p.id = (select distinct h.person.id from PersonChangeHistory h where UPPER(FUNCTION('REGEXP_REPLACE',h.idNumber,'[^a-zA-Z0-9]+','')) in (:idNumbers))
        or UPPER(FUNCTION('REPLACE',p.email,' ','')) = UPPER(:email)
        or UPPER(FUNCTION('REPLACE',p.privateEmail,' ','')) = UPPER(:privateEmail)
    """
    )
    fun findOneByPeselOrIdNumberOrEmailOrPrivateEmail(
        personId: Long?,
        pesel: String,
        idNumbers: List<String>,
        email: String,
        privateEmail: String
    ): Person?

    @Query(
        """
        select p
        from Person p
        where p.name = :name
        and p.surname = :surname
        and p.pesel is null
        and p.birthDate = :birthDate
        and UPPER(FUNCTION('REPLACE',p.email,' ','')) <> UPPER(:email)
        and UPPER(FUNCTION('REPLACE',p.privateEmail,' ','')) <> UPPER(:privateEmail)
        and (p.idNumber is null or UPPER(FUNCTION('REGEXP_REPLACE',p.idNumber,'[^a-zA-Z0-9]+','')) not in (:idNumbers))
    """
    )
    fun findPotentialDuplicate(
        name: String,
        surname: String,
        birthDate: Date,
        email: String,
        privateEmail: String,
        idNumbers: List<String>
    ): List<PersonBasicData>
}
