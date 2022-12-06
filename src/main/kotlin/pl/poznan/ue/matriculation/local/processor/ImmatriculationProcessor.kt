package pl.poznan.ue.matriculation.local.processor

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.service.ImmatriculationService

open class ImmatriculationProcessor(
    private val immatriculationService: ImmatriculationService,
    targetSystemProcessor: TargetSystemProcessor<Person>
) :
    ProcessDecorator<Person>(targetSystemProcessor) {
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    override fun process(processRequest: ProcessRequest): ProcessResult<Person> {
        return super.process(processRequest).also {
            val student = immatriculationService.immatriculate(
                person = it.person,
                import = processRequest.import,
                application = processRequest.application
            )
            it.assignedIndexNumber = student.indexNumber
        }
    }
}
