package pl.poznan.ue.matriculation.local.domain.applicants

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToOne
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId

@Entity
class CourseData(

    @OneToOne(mappedBy = "courseData", fetch = FetchType.LAZY)
    var erasmusData: ErasmusData? = null,

    var level: String?,

    var name: String?,

    var term: String?
) : BaseEntityLongId()
