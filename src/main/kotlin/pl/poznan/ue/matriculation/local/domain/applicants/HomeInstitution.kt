package pl.poznan.ue.matriculation.local.domain.applicants

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToOne
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId

@Entity
class HomeInstitution(


    @OneToOne(mappedBy = "homeInstitution", fetch = FetchType.LAZY)
    var erasmusData: ErasmusData? = null,

    var departmentName: String?,

    var erasmusCode: String?,

    var country: String?,

    var address: String?
) : BaseEntityLongId()
