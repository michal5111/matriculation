package pl.ue.poznan.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.ue.poznan.matriculation.local.domain.applicants.Document
import pl.ue.poznan.matriculation.local.domain.applicants.EducationData

@Repository
interface DocumentRepository: JpaRepository<Document, Long> {
    fun findByEducationDataAndCertificateTypeCode(educationData: EducationData, certificateTypeCode: String): Document?

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    fun deleteAllByEducationData(educationData: EducationData)
}