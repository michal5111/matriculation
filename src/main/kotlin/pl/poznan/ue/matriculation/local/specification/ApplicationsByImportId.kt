package pl.poznan.ue.matriculation.local.specification

import org.springframework.data.jpa.domain.Specification
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class ApplicationsByImportId(private val importId: Long?) : Specification<Application> {
    override fun toPredicate(
        root: Root<Application>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        return importId?.let {
            criteriaBuilder.equal(root.get<Import>("import").get<Long>("id"), importId)
        }
    }
}
