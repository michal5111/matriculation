package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import pl.poznan.ue.matriculation.oracle.domain.DocumentType

interface DocumentTypeRepository : JpaRepository<DocumentType, String>