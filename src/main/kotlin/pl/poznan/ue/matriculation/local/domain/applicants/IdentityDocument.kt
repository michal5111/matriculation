package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
class IdentityDocument(

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    val applicant: Applicant? = null,

    var country: String?,

    @Temporal(TemporalType.DATE)
    var expDate: Date?,

    var number: String?,

    var type: Char?
) : BaseEntityLongId() {


    override fun toString(): String {
        return "IdentityDocument(id=$id, documentCountry=$country, documentExpDate=$expDate, documentNumber=$number, documentType=$type)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdentityDocument

        if (country != other.country) return false
        if (expDate != other.expDate) return false
        if (number != other.number) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = country?.hashCode() ?: 0
        result = 31 * result + (expDate?.hashCode() ?: 0)
        result = 31 * result + (number?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        return result
    }


}