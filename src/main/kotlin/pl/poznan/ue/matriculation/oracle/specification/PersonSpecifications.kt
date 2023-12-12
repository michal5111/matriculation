package pl.poznan.ue.matriculation.oracle.specification

import org.springframework.data.jpa.domain.Specification
import pl.poznan.ue.matriculation.oracle.domain.Person

object PersonSpecifications {
    fun emailEquals(email: String): Specification<Person> = Specification<Person> { root, _, criteriaBuilder ->
        criteriaBuilder.equal(root.get<String>("email"), email)

    }

    fun idEquals(id: Long?): Specification<Person> = Specification<Person> { root, _, criteriaBuilder ->
        criteriaBuilder.equal(root.get<Long?>("id"), id)
    }

    fun peselEquals(pesel: String): Specification<Person> = Specification<Person> { root, _, criteriaBuilder ->
        criteriaBuilder.equal(root.get<String?>("pesel"), pesel)
    }

//    fun identityDocumentsIn(idNumbers: List<String>): Specification<Person> {
//        return Specification<Person> { root, _, criteriaBuilder ->
//            criteriaBuilder.`in`(root.get<String?>("pesel"), idNumbers)
//        }
//    }
}
