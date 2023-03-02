package pl.poznan.ue.matriculation.local.specification

import org.springframework.data.jpa.domain.Specification
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument
import pl.poznan.ue.matriculation.local.domain.applications.Application
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class ApplicationsByPesel(private val pesel: String?) : Specification<Application> {
    override fun toPredicate(
        root: Root<Application>,
        query: CriteriaQuery<*>,
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
