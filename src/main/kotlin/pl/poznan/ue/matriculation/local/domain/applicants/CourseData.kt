package pl.poznan.ue.matriculation.local.domain.applicants

import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToOne

@Entity
class CourseData(

    @OneToOne(mappedBy = "courseData", fetch = FetchType.LAZY)
    var erasmusData: ErasmusData? = null,

    var level: String?,

    var name: String?,

    var term: String?
) : BaseEntityLongId()
