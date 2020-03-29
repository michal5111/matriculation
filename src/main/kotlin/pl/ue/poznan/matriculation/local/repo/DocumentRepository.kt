package pl.ue.poznan.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.local.domain.applicants.Document
import pl.ue.poznan.matriculation.local.domain.applicants.EducationData

@Repository
interface DocumentRepository: JpaRepository<Document, Long> {
    fun findByEducationDataAndCertificateTypeCode(educationData: EducationData, certificateTypeCode: String): Document?
}