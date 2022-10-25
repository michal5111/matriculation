package pl.poznan.ue.matriculation.local.processor

import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.ApplicantForeignerData
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.OwnedDocument
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.repo.CitizenshipRepository
import pl.poznan.ue.matriculation.oracle.repo.DocumentTypeRepository
import pl.poznan.ue.matriculation.oracle.repo.OwnedDocumentRepository

class OwnedDocumentsProcessor(
    private val documentTypeRepository: DocumentTypeRepository,
    private val ownedDocumentRepository: OwnedDocumentRepository,
    private val citizenshipRepository: CitizenshipRepository,
    private val targetSystemProcessor: TargetSystemProcessor<Person?>
) : TargetSystemProcessor<Person?> {
    override fun process(processRequest: ProcessRequest): ProcessResult<Person?> {
        when (processRequest.application.applicant?.applicantForeignerData?.baseOfStay) {
            "OKP" -> createOrUpdateOkp(processRequest.person!!, processRequest.application.applicant!!)
        }
        return targetSystemProcessor.process(processRequest)
    }

    private fun createOrUpdateOkp(person: Person, applicant: Applicant) {
        val afd = applicant.applicantForeignerData ?: throw IllegalArgumentException("Applicant foreigner data is null")
        val bof = afd.baseOfStay ?: throw IllegalArgumentException("Base of stay is null")
        val ownedDocument = ownedDocumentRepository.findByPersonAndDocumentType(
            person,
            documentTypeRepository.getReferenceById(bof)
        )
        if (ownedDocument != null) {
            updateOkp(ownedDocument, bof, afd)
        } else {
            createOkp(person, afd)
        }
    }

    private fun createOkp(
        person: Person,
        it: ApplicantForeignerData
    ) {
        val baseOfStay = it.baseOfStay ?: return
        person.addOwnedDocument(
            OwnedDocument(
                documentType = documentTypeRepository.getReferenceById(baseOfStay),
                person = person,
                issueDate = it.polishCardIssueDate,
                issueCountry = it.polishCardIssueCountry?.let { countryCode ->
                    citizenshipRepository.getReferenceById(countryCode)
                },
                number = it.polishCardNumber,
                expirationDate = it.polishCardValidTo
            )
        )
    }

    private fun updateOkp(
        ownedDocument: OwnedDocument,
        bof: String,
        afd: ApplicantForeignerData
    ) = ownedDocument.apply {
        documentType = documentTypeRepository.getReferenceById(bof)
        issueDate = afd.polishCardIssueDate
        issueCountry = afd.polishCardIssueCountry?.let { countryCode ->
            citizenshipRepository.getReferenceById(countryCode)
        }
        number = afd.polishCardNumber
        expirationDate = afd.polishCardValidTo
    }
}
