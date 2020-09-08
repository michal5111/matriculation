package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.oracle.domain.School

@Repository
interface SchoolRepository : JpaRepository<School, Long> {
    fun findSchoolByErasmusCode(erasmusCode: String): School?

    @Query(nativeQuery = true, value = """
        select ID 
        from DZ_SZKOLY 
        where upper(utl_raw.cast_to_varchar2((nlssort(NAME, 'nls_sort=binary_ai')))) 
        like upper(utl_raw.cast_to_varchar2((nlssort(:name, 'nls_sort=binary_ai')))) 
        or upper(utl_raw.cast_to_varchar2((nlssort(NAZWA, 'nls_sort=binary_ai')))) 
        like upper(utl_raw.cast_to_varchar2((nlssort(:name, 'nls_sort=binary_ai'))))
    """)
    fun findByNameIgnoreCase(name: String): Long?
}