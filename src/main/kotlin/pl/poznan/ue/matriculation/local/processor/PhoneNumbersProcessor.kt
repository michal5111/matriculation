package pl.poznan.ue.matriculation.local.processor

import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.PhoneNumber
import pl.poznan.ue.matriculation.oracle.repo.PhoneNumberTypeRepository

class PhoneNumbersProcessor(
    private val phoneNumberTypeRepository: PhoneNumberTypeRepository,
    private val targetSystemProcessor: TargetSystemProcessor<Person?>
) : TargetSystemProcessor<Person?> {
    override fun process(processRequest: ProcessRequest): ProcessResult<Person?> {
        val person = processRequest.person!!
        val applicant = processRequest.application.applicant!!
        applicant.phoneNumbers.forEach { phoneNumber ->
            val personPhoneNumber = person.phoneNumbers.find {
                phoneNumber.phoneNumberType == it.phoneNumberType.code
            }
            if (personPhoneNumber != null) {
                personPhoneNumber.phoneNumberType =
                    phoneNumberTypeRepository.getReferenceById(phoneNumber.phoneNumberType)
                personPhoneNumber.comments = phoneNumber.comment
            } else {
                person.addPhoneNumber(
                    PhoneNumber(
                        person = person,
                        phoneNumberType = phoneNumberTypeRepository.getReferenceById(phoneNumber.phoneNumberType),
                        number = phoneNumber.number,
                        comments = phoneNumber.comment
                    )
                )
            }
        }
        return targetSystemProcessor.process(processRequest)
    }
}
