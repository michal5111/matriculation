package pl.poznan.ue.matriculation.cem.domain

import jakarta.persistence.*
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "courses")
class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    val name: String = ""

    @Column(name = "is_archived")
    val isArchived: Int = 0

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    val courseEditions: List<CourseEdition> = mutableListOf()
}
