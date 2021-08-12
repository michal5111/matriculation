package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class CourseData(
    @JsonIgnore
    @OneToOne(mappedBy = "courseData")
    var erasmusData: ErasmusData? = null,

    var level: String?,

    var name: String?,

    var term: String?
) : BaseEntityLongId()
