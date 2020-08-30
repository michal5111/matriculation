package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class CourseData(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @JsonIgnore
        @OneToOne(mappedBy = "courseData")
        var erasmusData: ErasmusData? = null,

        var level: String?,

        var name: String?,

        var term: String?
)
