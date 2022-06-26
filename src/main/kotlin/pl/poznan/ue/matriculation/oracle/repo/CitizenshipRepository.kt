package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.Citizenship

@Repository
interface CitizenshipRepository : JpaRepository<Citizenship, String> {

    @Query(
        """
        select
            c
        from Citizenship c
        where upper(c.code) = upper(:name)
         or upper(c.nationality) = upper(:name)
         or upper(c.country) = upper(:name)
         or upper(c.countryEng) = upper(:name)
    """
    )
    fun findByAnyName(name: String): Citizenship?
}
