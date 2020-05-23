package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pl.poznan.ue.matriculation.oracle.domain.PostalCode

interface PostalCodeRepository: JpaRepository<PostalCode, Long> {

    @Query("select distinct pc from PostalCode pc where pc.code = :code and lower(pc.post) like lower(:post)")
    fun findByCodeAndPostLike(@Param("code") postalCode: String, @Param("post") post: String): List<PostalCode>
}