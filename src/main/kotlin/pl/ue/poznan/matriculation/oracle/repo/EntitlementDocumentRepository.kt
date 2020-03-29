package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.oracle.domain.EntitlementDocument
import pl.ue.poznan.matriculation.oracle.domain.Person

@Repository
interface EntitlementDocumentRepository: JpaRepository<EntitlementDocument, Long> {

    fun existsByPersonAndType(person: Person, type: Char): Boolean
}