package pl.poznan.ue.matriculation.local.domain.applicants

import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import java.util.*
import javax.persistence.*

@Entity
class IdentityDocument(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    var applicant: Applicant? = null,

    var country: String?,

    @Temporal(TemporalType.DATE)
    var expDate: Date?,

    var number: String?,

    var type: Char?,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "primaryIdentityDocument")
    var primaryIdApplicant: Applicant? = null
) : BaseEntityLongId() {

    override fun toString(): String {
        return "IdentityDocument(id=$id, documentCountry=$country, documentExpDate=$expDate, documentNumber=$number, documentType=$type)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IdentityDocument) return false

        if (number != other.number) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = number?.hashCode() ?: 0
        result = 31 * result + (type?.hashCode() ?: 0)
        return result
    }


}
