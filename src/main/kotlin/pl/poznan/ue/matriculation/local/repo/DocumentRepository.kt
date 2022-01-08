package pl.poznan.ue.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document

@Repository
interface DocumentRepository : JpaRepository<Document, Long> {

    @Transactional(
        rollbackFor = [Exception::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager"
    )
    fun deleteAllByApplicant(applicant: Applicant)
}
