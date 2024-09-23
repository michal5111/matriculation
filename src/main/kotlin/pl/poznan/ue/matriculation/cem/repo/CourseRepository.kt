package pl.poznan.ue.matriculation.cem.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.cem.domain.Course

@Repository
interface CourseRepository : JpaRepository<Course, Long> {

    fun findAllByIsArchived(isArchived: Int): List<Course>
}
