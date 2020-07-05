package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.DocumentType
import pl.poznan.ue.matriculation.oracle.domain.OwnedDocument
import pl.poznan.ue.matriculation.oracle.domain.Person

@Repository
interface OwnedDocumentRepository : JpaRepository<OwnedDocument, Long> {
    fun findByPersonAndAndDocumentType(person: Person, documentType: DocumentType): OwnedDocument?
}