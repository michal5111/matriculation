package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.oracle.domain.EntitlementDocument

@Repository
interface EntitlementDocumentRepository : JpaRepository<EntitlementDocument, Long> {

    @Transactional
    fun existsByPersonIdAndType(personId: Long?, type: Char): Boolean

    @Transactional
    fun getByPersonIdAndTypeAndNumber(personId: Long?, type: Char, number: String): List<EntitlementDocument>?
}