package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Person

@Repository
interface PersonRepository : JpaRepository<Person, Long> {

    fun findOneByPesel(pesel: String): Person?

    fun findOneByIdNumber(idNumber: String): Person?

    fun findOneByEmail(email: String): Person?

    fun findOneByPrivateEmail(email: String): Person?

    fun findByPeselOrIdNumberOrEmailOrPrivateEmail(pesel: String, idNumber: String, email: String, privateEmail: String): Person?
}