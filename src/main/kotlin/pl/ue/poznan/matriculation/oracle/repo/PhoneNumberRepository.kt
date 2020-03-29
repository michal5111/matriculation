package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import pl.ue.poznan.matriculation.oracle.domain.Person
import pl.ue.poznan.matriculation.oracle.domain.PhoneNumber

interface PhoneNumberRepository: JpaRepository<PhoneNumber, Long> {

    fun findByPersonAndNumber(person: Person, phoneNumber: String): PhoneNumber?
}