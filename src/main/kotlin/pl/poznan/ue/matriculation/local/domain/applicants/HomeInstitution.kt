package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class HomeInstitution(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @JsonIgnore
        @OneToOne(mappedBy = "homeInstitution")
        var erasmusData: ErasmusData? = null,

        var departmentName: String?,

        var erasmusCode: String?,

        var country: String?,

        var address: String?
)
