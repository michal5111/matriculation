package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.PhoneNumber

interface PhoneNumberRepository: JpaRepository<PhoneNumber, Long> {

    fun findByPersonAndNumber(person: Person, phoneNumber: String): PhoneNumber?
}