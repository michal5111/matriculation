package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.oracle.domain.AddressType

@Repository
interface AddressTypeRepository: JpaRepository<AddressType, String>