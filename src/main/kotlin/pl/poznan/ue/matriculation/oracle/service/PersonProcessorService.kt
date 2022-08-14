package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.Student

@Service
class PersonProcessorService(
    private val personService: PersonService,
    private val immatriculationService: ImmatriculationService
) {

    private val logger: Logger = LoggerFactory.getLogger(PersonProcessorService::class.java)

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    fun process(
        application: Application, import: Import
    ): Pair<Person, Student> {
        logger.trace("Tworzę lub aktualizuję osobę")
        val applicant = application.applicant ?: throw ApplicantNotFoundException()
        val person: Person = personService.createOrUpdatePerson(applicant)
        logger.trace("Tworzę potwierdzenie immatrykulacji")
        val student = immatriculationService.immatriculate(
            person = person,
            import = import,
            application = application
        )
        logger.trace("Wykonuję operacje poimmatrykulacyjne")
        return Pair(person, student)
    }
}
