package pl.poznan.ue.matriculation.local.processor

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.OwnedDocument
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.repo.CitizenshipRepository
import pl.poznan.ue.matriculation.oracle.repo.DocumentTypeRepository
import pl.poznan.ue.matriculation.oracle.repo.OwnedDocumentRepository
import java.util.*

open class OwnedDocumentsProcessor(
    private val documentTypeRepository: DocumentTypeRepository,
    private val ownedDocumentRepository: OwnedDocumentRepository,
    private val citizenshipRepository: CitizenshipRepository,
    targetSystemProcessor: TargetSystemProcessor<Person>
) : ProcessDecorator<Person>(targetSystemProcessor) {
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    override fun process(processRequest: ProcessRequest): ProcessResult<Person> {
        return super.process(processRequest).also {
            when (processRequest.application.applicant?.applicantForeignerData?.baseOfStay) {
                "OKP" -> createOkp(it.person, processRequest.application.applicant ?: return@also)
            }
        }
    }

    private fun createOkp(person: Person, applicant: Applicant) {
        val afd = applicant.applicantForeignerData ?: throw IllegalArgumentException("Applicant foreigner data is null")
        val bof = afd.baseOfStay ?: throw IllegalArgumentException("Base of stay is null")
        val ownedDocument = afd.polishCardNumber?.let {
            ownedDocumentRepository.findByPersonAndDocumentTypeAndNumber(
                person,
                documentTypeRepository.getReferenceById(bof),
                it
            )
        }
        if (ownedDocument == null) {
            val baseOfStay = afd.baseOfStay ?: return
            person.addOwnedDocument(
                OwnedDocument(
                    documentType = documentTypeRepository.getReferenceById(baseOfStay),
                    person = person,
                    issueDate = afd.polishCardIssueDate ?: Date(),
                    issueCountry = afd.polishCardIssueCountry?.let { countryCode ->
                        citizenshipRepository.getReferenceById(countryCode)
                    },
                    number = afd.polishCardNumber,
                    expirationDate = afd.polishCardValidTo
                )
            )
        }
    }
}
