package pl.poznan.ue.matriculation.cem.repo

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.cem.domain.CourseEdition
import pl.poznan.ue.matriculation.cem.enum.CourseEditionStatus

@Repository
interface CourseEditionRepository : JpaRepository<CourseEdition, Long> {

    @EntityGraph("courseEdition.course")
    fun findAllByStatus(status: CourseEditionStatus, sort: Sort): List<CourseEdition>
}
