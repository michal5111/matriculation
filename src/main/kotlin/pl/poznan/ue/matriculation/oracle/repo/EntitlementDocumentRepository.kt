package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.EntitlementDocument
import pl.poznan.ue.matriculation.oracle.domain.Person

@Repository
interface EntitlementDocumentRepository : JpaRepository<EntitlementDocument, Long> {

    fun existsByPersonAndType(person: Person, type: Char): Boolean

    fun getByPersonAndTypeAndNumber(person: Person, type: Char, number: String): EntitlementDocument?
}