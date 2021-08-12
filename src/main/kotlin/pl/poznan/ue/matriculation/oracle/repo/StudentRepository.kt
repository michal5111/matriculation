package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Student

@Repository
interface StudentRepository : JpaRepository<Student, Long>, IndexNumberRepository {

    fun findByPersonIdAndIndexTypeCodeOrderByIndexNumberAsc(personId: Long?, indexType: String): List<Student>

    fun findByPersonIdAndIndexTypeCodeOrderByIndexNumberAsc(personId: Long, indexTypeCode: String): List<Student>

    fun findByPersonIdAndMainIndex(personId: Long?, isMain: Char): Student?
}