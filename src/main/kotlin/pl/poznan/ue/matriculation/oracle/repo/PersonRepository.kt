package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.oracle.domain.Person

@Repository
interface PersonRepository: JpaRepository<Person, Long> {

    @Transactional(rollbackFor = [java.lang.Exception::class], propagation = Propagation.REQUIRED, transactionManager = "oracleTransactionManager")
    fun findOneByPesel(pesel: String): Person?

    @Transactional(rollbackFor = [java.lang.Exception::class], propagation = Propagation.REQUIRED, transactionManager = "oracleTransactionManager")
    fun findOneByIdNumber(idNumber: String): Person?
}