package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class CoordinatorData(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @JsonIgnore
        @OneToOne(mappedBy = "coordinatorData")
        var erasmusData: ErasmusData? = null,

        var email: String?,

        var name: String?,

        var phone: String?
)
