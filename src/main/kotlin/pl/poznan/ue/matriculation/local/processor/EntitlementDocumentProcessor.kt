package pl.poznan.ue.matriculation.local.processor

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.EntitlementDocument
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository

open class EntitlementDocumentProcessor(
    private val schoolRepository: SchoolRepository,
    targetSystemProcessor: TargetSystemProcessor<Person>
) : ProcessDecorator<Person>(targetSystemProcessor) {
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    override fun process(processRequest: ProcessRequest): ProcessResult<Person> {
        return super.process(processRequest).also { processResult ->
            val person = processResult.person
            val applicant = processRequest.application.applicant ?: return@also
            applicant.documents.filter {
                it.certificateUsosCode != null
            }.filterNot {
                person.entitlementDocuments.any { entitlementDocument ->
                    it.certificateUsosCode == entitlementDocument.type
                }
            }.forEach {
                val certificateUsosCode = it.certificateUsosCode ?: '?'
                person.addEntitlementDocument(
                    EntitlementDocument(
                        person = person,
                        issueDate = it.issueDate,
                        description = it.issueInstitution.takeIf { _ ->
                            it.issueInstitutionUsosCode == null
                        },
                        number = it.documentNumber,
                        type = certificateUsosCode,
                        school = it.issueInstitutionUsosCode?.let { schoolId ->
                            schoolRepository.getReferenceById(schoolId)
                        }
                    )
                )
            }
        }
    }
}
