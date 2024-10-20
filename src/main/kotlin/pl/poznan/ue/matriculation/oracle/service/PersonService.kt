package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.entityRepresentations.PersonBasicData
import pl.poznan.ue.matriculation.oracle.repo.PersonRepository
import java.util.*

@Service
@Transactional(transactionManager = "oracleTransactionManager")
class PersonService(
    private val personRepository: PersonRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(PersonService::class.java)

    fun save(person: Person): Person {
        return personRepository.save(person)
    }

    fun findOneByPeselOrIdNumberOrPersonId(
        personId: Long?,
        pesel: String,
        idNumbers: List<String>
    ): Person? {
        return personRepository.findOneByPeselOrIdNumberOrPersonId(personId, pesel, idNumbers)
    }

    fun findPotentialDuplicate(
        name: String,
        surname: String,
        birthDate: Date,
        pesel: String?,
        idNumbers: List<String>
    ): List<PersonBasicData> {
        return if (pesel != null) {
            personRepository.findPotentialDuplicate(name, surname, birthDate, idNumbers)
        } else {
            personRepository.findPotentialDuplicateWithNullPesel(name, surname, birthDate, idNumbers)
        }
    }
}
