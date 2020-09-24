package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.IndexType
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.Student

@Repository
interface StudentRepository : JpaRepository<Student, Long> {

    fun findByPersonAndIndexType(person: Person, indexType: IndexType): Student?

    fun findByPersonIdAndIndexTypeCode(personId: Long, indexTypeCode: String): Student?

    fun findByPersonAndMainIndex(person: Person, isMain: Char): Student?
}