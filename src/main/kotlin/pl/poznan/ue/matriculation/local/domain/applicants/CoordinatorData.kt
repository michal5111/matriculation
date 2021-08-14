package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class CoordinatorData(
    @JsonIgnore
    @OneToOne(mappedBy = "coordinatorData")
    var erasmusData: ErasmusData? = null,

    var email: String?,

    var name: String?,

    var phone: String?
) : BaseEntityLongId()
