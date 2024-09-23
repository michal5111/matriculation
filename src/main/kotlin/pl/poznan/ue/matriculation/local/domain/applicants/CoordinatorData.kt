package pl.poznan.ue.matriculation.local.domain.applicants

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToOne
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId

@Entity
class CoordinatorData(

    @OneToOne(mappedBy = "coordinatorData", fetch = FetchType.LAZY)
    var erasmusData: ErasmusData? = null,

    var email: String?,

    var name: String?,

    var phone: String?
) : BaseEntityLongId()
