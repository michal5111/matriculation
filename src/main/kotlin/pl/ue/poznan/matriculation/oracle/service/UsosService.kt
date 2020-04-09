package pl.ue.poznan.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.ue.poznan.matriculation.oracle.dto.IndexTypeDto
import pl.ue.poznan.matriculation.oracle.repo.IndexTypeRepository
import pl.ue.poznan.matriculation.oracle.repo.ProgrammeStageRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Service
class UsosService(
        private val indexTypeRepository: IndexTypeRepository,
        private val programmeStageRepository: ProgrammeStageRepository
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
                .createQuery("SELECT dc.code FROM DidacticCycle dc WHERE dc.code LIKE :didacticCycleCode AND dc.didacticCycleType.code = 'SEM'", String::class.java)
        return query!!.setParameter("didacticCycleCode", "${didacticCycleCode}%")
                .setMaxResults(maxResults)
                .resultList
    }
}