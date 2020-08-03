package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
class IdentityDocument(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        val applicant: Applicant? = null,

        var country: String?,

        var expDate: Date?,

        var number: String?,

        var type: Char?
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdentityDocument

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "IdentityDocument(id=$id, documentCountry=$country, documentExpDate=$expDate, documentNumber=$number, documentType=$type)"
    }


}