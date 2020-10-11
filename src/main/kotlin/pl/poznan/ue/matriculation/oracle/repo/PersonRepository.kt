package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Person

@Repository
interface PersonRepository : JpaRepository<Person, Long> {

    fun findOneByPesel(pesel: String): Person?

    @Query("select p from Person p where UPPER(FUNCTION('REPLACE',p.idNumber,' ','')) = UPPER(FUNCTION('REPLACE',:idNumber,' ',''))")
    fun findOneByIdNumber(idNumber: String): Person?

    @Query("select p from Person p where UPPER(FUNCTION('REPLACE',p.email,' ','')) = UPPER(FUNCTION('REPLACE',:email,' ',''))")
    fun findOneByEmail(email: String): Person?

    @Query("select p from Person p where UPPER(FUNCTION('REPLACE',p.privateEmail,' ','')) = UPPER(FUNCTION('REPLACE',:privateEmail,' ',''))")
    fun findOneByPrivateEmail(privateEmail: String): Person?
}