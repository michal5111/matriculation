package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.DocumentType
import pl.poznan.ue.matriculation.oracle.domain.OwnedDocument
import pl.poznan.ue.matriculation.oracle.domain.Person
import java.time.LocalDate

@Repository
interface OwnedDocumentRepository : JpaRepository<OwnedDocument, Long> {
    fun findByPersonAndDocumentTypeAndNumber(person: Person, documentType: DocumentType, number: String): OwnedDocument?

    @Query(
        """
        select max(od.expirationDate)
        from OwnedDocument od
        where od.documentType.code = :documentTypeCode
        and od.person.id = :personId
    """
    )
    fun findMaxExpirationDateByDocumentTypeAndPersonId(documentTypeCode: String, personId: Long): LocalDate?
}
