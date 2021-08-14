package pl.poznan.ue.matriculation.local.domain.applicants

import pl.poznan.ue.matriculation.local.domain.BaseEntityApplicantId
import java.io.Serializable
import javax.persistence.Entity

@Entity
class Name(

    var middle: String?,
    var family: String,
    var given: String,
    var maiden: String?,
    applicant: Applicant? = null
) : BaseEntityApplicantId(applicant), Serializable {

    override fun toString(): String {
        return "Name(middle=$middle, family=$family, given=$given, maiden=$maiden)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Name

        if (applicant != other.applicant) return false

        return true
    }

    override fun hashCode(): Int {
        return applicant?.hashCode() ?: 0
    }


}