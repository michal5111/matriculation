package pl.poznan.ue.matriculation.oracle.service

import org.hibernate.exception.GenericJDBCException
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.exception.IndexChangeException
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.oracle.dto.IndexTypeDto
import pl.poznan.ue.matriculation.oracle.repo.DidacticCycleRepository
import pl.poznan.ue.matriculation.oracle.repo.IndexTypeRepository
import pl.poznan.ue.matriculation.oracle.repo.ProgrammeStageRepository
import pl.poznan.ue.matriculation.oracle.repo.StudentRepository


@Service
class UsosService(
    private val indexTypeRepository: IndexTypeRepository,
    private val programmeStageRepository: ProgrammeStageRepository,
    private val studentRepository: StudentRepository,
    private val applicantRepository: ApplicantRepository,
    private val didacticCycleRepository: DidacticCycleRepository
) {

    fun getAvailableIndexPoolsCodes(): List<IndexTypeDto> {
        return indexTypeRepository.getIndexTypeCodes()
    }

    fun getStageByProgrammeCode(programmeCode: String): List<String> {
        return programmeStageRepository.getAllStageCodesByProgrammeCode(programmeCode)
    }

    fun findDidacticCycleCodes(didacticCycleCode: String, maxResults: Int): List<String> {
        return didacticCycleRepository.findDidacticCycleCodes(didacticCycleCode, maxResults)
    }

    fun updateIndexNumberByUsosIdAndIndexType(usosId: Long, indexTypeCode: String, newIndexNumber: String) {
        val students = studentRepository.findByPersonIdAndIndexTypeCodeOrderByIndexNumberAsc(usosId, indexTypeCode)
        val student = students.last()
        val applicant = applicantRepository.findByUsosId(usosId) ?: throw ApplicantNotFoundException()
        student.indexNumber = newIndexNumber
        applicant.assignedIndexNumber = newIndexNumber
        try {
            studentRepository.save(student)
            applicantRepository.save(applicant)
        } catch (e: Exception) {
            var t: Throwable? = e
            while (t != null) {
                if (t is GenericJDBCException) {
                    throw IndexChangeException("${t.sqlException} ${t.message}", e)
                }
                t = t.cause
            }
            throw IndexChangeException(e.message, e)
        }
    }
}
