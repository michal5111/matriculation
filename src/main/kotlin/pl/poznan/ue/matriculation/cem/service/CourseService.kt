package pl.poznan.ue.matriculation.cem.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.cem.domain.Course
import pl.poznan.ue.matriculation.cem.domain.CourseEdition
import pl.poznan.ue.matriculation.cem.enum.CourseEditionStatus
import pl.poznan.ue.matriculation.cem.repo.CourseEditionRepository
import pl.poznan.ue.matriculation.cem.repo.CourseRepository

@Service
@ConditionalOnProperty(
    value = ["pl.poznan.ue.matriculation.cem.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class CourseService(
    private val courseRepository: CourseRepository,
    private val courseEditionRepository: CourseEditionRepository
) {

    fun findAllByIsArchived(isArchived: Int): List<Course> {
        return courseRepository.findAllByIsArchived(isArchived)
    }

    fun findAllByStatus(status: CourseEditionStatus, sort: Sort): List<CourseEdition> {
        return courseEditionRepository.findAllByStatus(status, sort)
    }

    fun findAll(sort: Sort): MutableIterable<CourseEdition> = courseEditionRepository.findAll(sort)
}
