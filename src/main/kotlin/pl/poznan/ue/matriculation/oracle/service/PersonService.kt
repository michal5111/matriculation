package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.oracle.entityRepresentations.PersonBasicData
import pl.poznan.ue.matriculation.oracle.repo.PersonRepository
import java.util.*

@Service
class PersonService(
    private val personRepository: PersonRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(PersonService::class.java)

    fun findPotentialDuplicate(
        name: String,
        surname: String,
        birthDate: Date,
        email: String,
        privateEmail: String,
        idNumbers: List<String>
    ): List<PersonBasicData> {
        return personRepository.findPotentialDuplicate(name, surname, birthDate, email, privateEmail, idNumbers)
    }
}
