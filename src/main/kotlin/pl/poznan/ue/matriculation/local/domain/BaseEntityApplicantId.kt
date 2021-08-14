package pl.poznan.ue.matriculation.local.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import javax.persistence.*

@MappedSuperclass
open class BaseEntityApplicantId(
    @JsonIgnore
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    var applicant: Applicant? = null
) : BaseEntity() {
    @Id
    var applicantId: Long? = null
}