package pl.poznan.ue.matriculation.local.processor

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.PhoneNumber
import pl.poznan.ue.matriculation.oracle.repo.PhoneNumberTypeRepository

open class PhoneNumbersProcessor(
    private val phoneNumberTypeRepository: PhoneNumberTypeRepository,
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

            applicant.phoneNumbers.takeIf { it.isNotEmpty() }?.map { phoneNumber ->
                val found = person.phoneNumbers.find { it.number == phoneNumber.number }
                if (found != null) {
                    found.comments = phoneNumber.comment
                    found
                } else {
                    PhoneNumber(
                        person = person,
                        phoneNumberType = phoneNumberTypeRepository.getReferenceById(phoneNumber.phoneNumberType),
                        number = phoneNumber.number,
                        comments = phoneNumber.comment
                    )
                }
            }?.let {
                person.phoneNumbers.clear()
                person.phoneNumbers.addAll(it)
            }
        }
    }
}
