package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.oracle.domain.IndexType
import pl.ue.poznan.matriculation.oracle.domain.Person
import pl.ue.poznan.matriculation.oracle.domain.Student

@Repository
interface StudentRepository: JpaRepository<Student, Long> {

    fun findByPersonAndIndexType(person: Person, indexType: IndexType): Student?

    fun findByPersonIdAndIndexTypeCode(personId: Long, indexTypeCode: String): Student?
}