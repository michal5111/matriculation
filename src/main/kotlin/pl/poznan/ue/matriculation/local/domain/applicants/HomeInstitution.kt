package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class HomeInstitution(

        @JsonIgnore
        @OneToOne(mappedBy = "homeInstitution")
        var erasmusData: ErasmusData? = null,

        var departmentName: String?,

        var erasmusCode: String?,

        var country: String?,

        var address: String?
) : BaseEntityLongId()
