package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.Student
import pl.poznan.ue.matriculation.oracle.repo.IndexTypeRepository
import pl.poznan.ue.matriculation.oracle.repo.OrganizationalUnitRepository
import pl.poznan.ue.matriculation.oracle.repo.StudentRepository


@Service
class StudentService(
    private val indexTypeRepository: IndexTypeRepository,
    private val organizationalUnitRepository: OrganizationalUnitRepository,
    private val studentRepository: StudentRepository,
) {
    val logger: Logger = LoggerFactory.getLogger(StudentService::class.java)

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitCode: String

    @Transactional(
        rollbackFor = [java.lang.Exception::class, java.lang.RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    fun createOrFindStudent(person: Person, indexPoolCode: String): Student {
        person.students.findLast {
            it.indexType.code == indexPoolCode
        }?.let {
            return it
        }
        val indexNumberDto = studentRepository.getNewIndexNumber(indexPoolCode)
        val organizationalUnit = if (indexNumberDto.organizationalUnitCode != null)
            organizationalUnitRepository.getReferenceById(indexNumberDto.organizationalUnitCode)
        else organizationalUnitRepository.getReferenceById(defaultStudentOrganizationalUnitCode)
        var isNewIndexDefault = false
        val currentMainIndex = person.students.find {
            it.mainIndex
        }
        if (currentMainIndex == null) {
            isNewIndexDefault = true
        } else if (organizationalUnit.code == defaultStudentOrganizationalUnitCode) {
            currentMainIndex.id?.let {
                studentRepository.setMainIndex(it, false)
            }
            isNewIndexDefault = true
        }
        val student = Student(
            indexType = indexTypeRepository.getReferenceById(indexPoolCode),
            organizationalUnit = organizationalUnit,
            indexNumber = indexNumberDto.number,
            mainIndex = isNewIndexDefault,
            person = person
        )
        person.addStudent(student)
        return studentRepository.save(student)
    }
}
