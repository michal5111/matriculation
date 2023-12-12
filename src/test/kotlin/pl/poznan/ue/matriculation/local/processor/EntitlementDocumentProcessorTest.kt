//package pl.poznan.ue.matriculation.local.processor
//
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito.*
//import org.springframework.transaction.PlatformTransactionManager
//import org.springframework.transaction.TransactionStatus
//import org.springframework.transaction.support.DefaultTransactionDefinition
//import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
//import pl.poznan.ue.matriculation.local.dto.ApplicationDto
//import pl.poznan.ue.matriculation.local.dto.ProcessResult
//import pl.poznan.ue.matriculation.oracle.domain.EntitlementDocument
//import pl.poznan.ue.matriculation.oracle.domain.Person
//import pl.poznan.ue.matriculation.oracle.domain.School
//import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository
//import java.time.LocalDate
//
//class EntitlementDocumentProcessorTest {
//
//    @Test
//    fun `process should add entitlement documents to person`() {
//        // Arrange
//        val schoolRepository = mock(SchoolRepository::class.java)
//        val targetSystemProcessor = mock(TargetSystemProcessor::class.java) as TargetSystemProcessor<Person>
//        val transactionManager = mock(PlatformTransactionManager::class.java)
//        val transactionStatus = mock(TransactionStatus::class.java)
//        `when`(transactionManager.getTransaction(any(DefaultTransactionDefinition::class.java)))
//            .thenReturn(transactionStatus)
//        val processor = EntitlementDocumentProcessor(schoolRepository, targetSystemProcessor)
//        val application = ApplicationDto(applicant = Applicant())
//        val processRequest = ProcessRequest(application, emptyMap())
//        val person = Person(surname = "Doe", name = "John")
//        `when`(targetSystemProcessor.process(processRequest)).thenReturn(ProcessResult(person))
//        val entitlementDocument = EntitlementDocument(
//            person = person,
//            issueDate = LocalDate.now(),
//            description = "Document description",
//            number = "123",
//            type = "CertificateUsosCode",
//            school = School(id = "SchoolId", name = "School Name")
//        )
//        `when`(schoolRepository.getReferenceById("SchoolId")).thenReturn(entitlementDocument.school)
//
//        // Act
//        val result = processor.process(processRequest)
//
//        // Assert
//        verify(targetSystemProcessor).process(processRequest)
//        assertTrue(result.person.entitlementDocuments.contains(entitlementDocument))
//        verify(schoolRepository).getReferenceById("SchoolId")
//        verify(transactionManager).getTransaction(any(DefaultTransactionDefinition::class.java))
//        verify(transactionStatus).flush()
//        verify(transactionManager).commit(transactionStatus)
//    }
//}
