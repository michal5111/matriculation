package pl.poznan.ue.matriculation.local.domain.applicants


import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import pl.poznan.ue.matriculation.local.domain.applications.Application
import java.io.Serializable
import java.util.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            name = "DocumentUniqueConstraint",
            columnNames = ["applicant_id", "documentNumber", "certificateTypeCode"]
        )
    ]
)
class Document(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    var applicant: Applicant? = null,

    val certificateType: String,

    val certificateTypeCode: String,

    val certificateUsosCode: Char?,


    @Basic(fetch = FetchType.LAZY)
    @Lob
    var comment: String?,

    var documentNumber: String?,

    var documentYear: Int?,

    var issueCity: String?,

    var issueCountry: String?,

    @Temporal(TemporalType.DATE)
    var issueDate: Date?,

    var issueInstitution: String?,

    var issueInstitutionUsosCode: Long?,

    var modificationDate: Date?,

    @get:JsonIgnore
    @OneToMany(mappedBy = "certificate", fetch = FetchType.LAZY)
    val Applications: MutableList<Application> = mutableListOf()

) : BaseEntityLongId(), Serializable {

    override fun toString(): String {
        return "Document(id=$id, certificateType='$certificateType', certificateTypeCode='$certificateTypeCode" +
            "', certificateUsosCode=$certificateUsosCode, comment=$comment, documentNumber=$documentNumber, " +
            "documentYear=$documentYear, issueCity=$issueCity, issueCountry=$issueCountry, " +
            "issueDate=$issueDate, issueInstitution=$issueInstitution, " +
            "issueInstitutionUsosCode=$issueInstitutionUsosCode, modificationDate=$modificationDate)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Document) return false

        if (certificateUsosCode != other.certificateUsosCode) return false
        if (documentNumber != other.documentNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = certificateUsosCode?.hashCode() ?: 0
        result = 31 * result + documentNumber.hashCode()
        return result
    }


}
