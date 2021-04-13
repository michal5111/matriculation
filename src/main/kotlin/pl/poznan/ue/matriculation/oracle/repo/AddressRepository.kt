package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Address
import pl.poznan.ue.matriculation.oracle.domain.AddressType
import pl.poznan.ue.matriculation.oracle.domain.Person

@Repository
interface AddressRepository : JpaRepository<Address, String> {

    fun findByPersonAndAddressType(person: Person, addressType: AddressType): Address?

    fun existsByPersonAndAddressType(person: Person, addressType: AddressType): Boolean
}