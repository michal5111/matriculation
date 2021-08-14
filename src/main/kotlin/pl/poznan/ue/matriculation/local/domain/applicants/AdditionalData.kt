package pl.poznan.ue.matriculation.local.domain.applicants


import pl.poznan.ue.matriculation.local.domain.BaseEntityApplicantId
import java.io.Serializable
import javax.persistence.Entity

@Entity
class AdditionalData(

    var fathersName: String?,

    var militaryCategory: String?,

    var militaryStatus: String?,

    var mothersName: String?,

    var wku: String?,

    applicant: Applicant? = null
) : BaseEntityApplicantId(applicant), Serializable {

    override fun toString(): String {
        return "AdditionalData(fathersName=$fathersName, militaryCategory=$militaryCategory, " +
                "militaryStatus=$militaryStatus, mothersName=$mothersName, wku=$wku)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdditionalData

        if (applicant != other.applicant) return false

        return true
    }

    override fun hashCode(): Int {
        return applicant?.hashCode() ?: 0
    }


}