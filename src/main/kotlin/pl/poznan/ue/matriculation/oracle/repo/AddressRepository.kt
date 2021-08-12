package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Address
import pl.poznan.ue.matriculation.oracle.domain.AddressType

@Repository
interface AddressRepository : JpaRepository<Address, String> {

    fun findByPersonIdAndAddressType(personId: Long?, addressType: AddressType): Address?

    fun existsByPersonIdAndAddressType(personId: Long?, addressType: AddressType): Boolean
}