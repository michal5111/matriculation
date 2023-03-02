package pl.poznan.ue.matriculation.local.domain.applicants

import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToOne

@Entity
class CoordinatorData(

    @OneToOne(mappedBy = "coordinatorData", fetch = FetchType.LAZY)
    var erasmusData: ErasmusData? = null,

    var email: String?,

    var name: String?,

    var phone: String?
) : BaseEntityLongId()
