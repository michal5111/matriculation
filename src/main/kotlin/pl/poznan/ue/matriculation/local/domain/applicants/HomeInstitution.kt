package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToOne

@Entity
class HomeInstitution(

    @JsonIgnore
    @OneToOne(mappedBy = "homeInstitution", fetch = FetchType.LAZY)
    var erasmusData: ErasmusData? = null,

    var departmentName: String?,

    var erasmusCode: String?,

    var country: String?,

    var address: String?
) : BaseEntityLongId()
