package pl.poznan.ue.matriculation.cem.repo

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pl.poznan.ue.matriculation.cem.domain.Course

@Repository
interface CourseRepository : PagingAndSortingRepository<Course, Long> {

    fun findAllByIsArchived(isArchived: Int): List<Course>
}
