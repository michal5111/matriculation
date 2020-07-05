package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.OrganizationalUnit

@Repository
interface OrganizationalUnitRepository : JpaRepository<OrganizationalUnit, String>