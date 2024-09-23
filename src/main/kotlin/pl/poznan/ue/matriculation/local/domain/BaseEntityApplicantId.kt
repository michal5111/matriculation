package pl.poznan.ue.matriculation.local.domain

import jakarta.persistence.*
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant

@MappedSuperclass
open class BaseEntityApplicantId(
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    open var applicant: Applicant? = null
) : BaseEntity() {
    @Id
    var applicantId: Long? = null
}
