package pl.poznan.ue.matriculation.local.domain.applicants

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import java.io.Serializable

@Entity
class Status(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicantForeignerData_id", referencedColumnName = "applicant_id")
    var applicantForeignerData: ApplicantForeignerData? = null,

    var status: String
) : BaseEntityLongId(), Serializable {

    override fun toString(): String {
        return "Status(id=$id, status='$status')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Status) return false

        if (applicantForeignerData != other.applicantForeignerData) return false
        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        var result = applicantForeignerData?.hashCode() ?: 0
        result = 31 * result + status.hashCode()
        return result
    }


}
