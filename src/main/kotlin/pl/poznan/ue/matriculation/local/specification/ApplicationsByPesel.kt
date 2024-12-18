package pl.poznan.ue.matriculation.local.specification

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument
import pl.poznan.ue.matriculation.local.domain.applications.Application

class ApplicationsByPesel(private val pesel: String?) : Specification<Application> {
    override fun toPredicate(
        root: Root<Application>,
        query: CriteriaQuery<*>?,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        return pesel?.let {
            criteriaBuilder.or(
                criteriaBuilder.like(
                    criteriaBuilder.upper(root.get<Applicant>("applicant").get("pesel")),
                    "${pesel.uppercase()}%"
                ),
                criteriaBuilder.like(
                    criteriaBuilder.upper(
                        root.get<Applicant>("applicant").get<IdentityDocument>("primaryIdentityDocument").get("number")
                    ),
                    "${pesel.uppercase()}%"
                ),
            )
        }
    }
}
