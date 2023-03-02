package pl.poznan.ue.matriculation.local.domain.applicants

import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import javax.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            name = "phoneNumberUniqueConstraint",
            columnNames = ["applicant_id", "number", "phoneNumberType"]
        )
    ]
)
class PhoneNumber(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    var applicant: Applicant? = null,

    var number: String,

    var phoneNumberType: String,

    var comment: String
) : BaseEntityLongId() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneNumber) return false

        if (number != other.number) return false
        if (phoneNumberType != other.phoneNumberType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = number.hashCode()
        result = 31 * result + phoneNumberType.hashCode()
        return result
    }
}
