package pl.ue.poznan.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.ue.poznan.matriculation.exception.IndexChangeException
import pl.ue.poznan.matriculation.local.repo.ApplicantRepository
import pl.ue.poznan.matriculation.oracle.dto.IndexTypeDto
import pl.ue.poznan.matriculation.oracle.repo.IndexTypeRepository
import pl.ue.poznan.matriculation.oracle.repo.ProgrammeStageRepository
import pl.ue.poznan.matriculation.oracle.repo.StudentRepository
import java.sql.SQLException
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Service
class UsosService(
        private val indexTypeRepository: IndexTypeRepository,
        private val programmeStageRepository: ProgrammeStageRepository,
        private val studentRepository: StudentRepository,
        private val applicantRepository: ApplicantRepository
) {

    @PersistenceContext(unitName = "oracle")
    private lateinit var entityManager: EntityManager

    fun getAvailableIndexPoolsCodes(): List<IndexTypeDto> {
        return indexTypeRepository.getIndexTypeCodes()
    }

    fun getStageByProgrammeCode(programmeCode: String): List<String> {
        return programmeStageRepository.getAllStageCodesByProgrammeCode(programmeCode)
    }

    fun findDidacticCycleCodes(didacticCycleCode: String, maxResults: Int): List<String> {
        val query = entityManager
                .createQuery("SELECT dc.code FROM DidacticCycle dc WHERE dc.code LIKE :didacticCycleCode AND dc.didacticCycleType.code = 'SEM' ORDER BY dc.dateTo DESC", String::class.java)
        return query!!.setParameter("didacticCycleCode", "${didacticCycleCode}%")
                .setMaxResults(maxResults)
                .resultList
    }

    fun updateIndexNumberByUsosIdAndIndexType(usosId: Long, indexTypeCode: String, newIndexNumber: String) {
        val student = studentRepository.findByPersonIdAndIndexTypeCode(usosId, indexTypeCode)
                ?: throw IllegalStateException("IndexNotFound")
        val applicant = applicantRepository.findByUsosId(usosId)
                ?: throw java.lang.IllegalStateException("Applicant not found")
        student.apply {
            indexNumber = newIndexNumber
        }
        applicant.apply {
            assignedIndexNumber = newIndexNumber
        }
        try {
            studentRepository.save(student)
            applicantRepository.save(applicant)
        } catch (e: Exception) {
            var t: Throwable? = e
            while (t!!.cause != null) {
                if (t is SQLException) {
                    println("${t.errorCode} ${t.sqlState}")
                    throw IndexChangeException(t.message, e)
                }
                t = t.cause
            }
            throw IndexChangeException(e.message, e)
        }
    }
}