package pl.poznan.ue.matriculation.local.specification

import org.springframework.data.jpa.domain.Specification
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applications.Application
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class ApplicationsByName(private val name: String?) : Specification<Application> {
    override fun toPredicate(
        root: Root<Application>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        return name?.let {
            criteriaBuilder.like(
                criteriaBuilder.upper(root.get<Applicant>("applicant").get("given")),
                "${name.uppercase()}%"
            )
        }
    }
}
