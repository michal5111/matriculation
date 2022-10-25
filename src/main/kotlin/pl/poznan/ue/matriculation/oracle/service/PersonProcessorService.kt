package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.local.processor.ProcessRequest
import pl.poznan.ue.matriculation.local.processor.TargetSystemProcessor
import pl.poznan.ue.matriculation.oracle.domain.Person

@Service
class PersonProcessorService(
    private val personService: PersonService,
    private val immatriculationService: ImmatriculationService
) : TargetSystemProcessor<Person?> {

    private val logger: Logger = LoggerFactory.getLogger(PersonProcessorService::class.java)

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    override fun process(
        processRequest: ProcessRequest
    ): ProcessResult<Person?> {
        logger.trace("Tworzę lub aktualizuję osobę")
        val applicant = processRequest.application.applicant ?: throw ApplicantNotFoundException()
        processRequest.person = personService.createOrUpdatePerson(applicant)
        logger.trace("Tworzę potwierdzenie immatrykulacji")
        val student = immatriculationService.immatriculate(
            person = processRequest.person!!,
            import = processRequest.import,
            application = processRequest.application
        )
        logger.trace("Wykonuję operacje poimmatrykulacyjne")
        return ProcessResult(
            processRequest.person!!.id ?: throw IllegalStateException(),
            student.indexNumber,
            processRequest.person!!
        )
    }
}
