package pl.poznan.ue.matriculation.local.processor

import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.EntitlementDocument
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository

class EntitlementDocumentProcessor(
    private val schoolRepository: SchoolRepository,
    private val targetSystemProcessor: TargetSystemProcessor<Person?>
) : TargetSystemProcessor<Person?> {
    override fun process(processRequest: ProcessRequest): ProcessResult<Person?> {
        val person = processRequest.person!!
        val applicant = processRequest.application.applicant!!
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
        return targetSystemProcessor.process(processRequest)
    }
}
