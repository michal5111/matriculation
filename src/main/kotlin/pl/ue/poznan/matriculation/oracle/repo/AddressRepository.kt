package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.oracle.domain.Address
import pl.ue.poznan.matriculation.oracle.domain.AddressType
import pl.ue.poznan.matriculation.oracle.domain.Person

@Repository
interface AddressRepository: JpaRepository<Address, String> {

    fun findByPersonAndAddressType(person: Person, addressType: AddressType): Address?
}