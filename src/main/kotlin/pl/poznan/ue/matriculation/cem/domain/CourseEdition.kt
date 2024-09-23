package pl.poznan.ue.matriculation.cem.domain

import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import pl.poznan.ue.matriculation.cem.enum.CourseEditionStatus

@Entity
@Immutable
@Table(name = "course_editions")
@NamedEntityGraph(
    name = "courseEdition.course",
    attributeNodes = [
        NamedAttributeNode("course")
    ]
)
class CourseEdition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    var course: Course? = null

    var name: String = ""

    @Enumerated(EnumType.ORDINAL)
    val status: CourseEditionStatus = CourseEditionStatus.START

    val number: String? = null
}
