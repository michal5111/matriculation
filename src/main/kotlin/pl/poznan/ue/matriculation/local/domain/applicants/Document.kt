package pl.poznan.ue.matriculation.local.domain.applicants


import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Document(

        @JsonIgnore
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "education_data_id", referencedColumnName = "applicant_id", nullable = false)
        var educationData: EducationData? = null,

        val certificateType: String,

        val certificateTypeCode: String,

        val certificateUsosCode: Char?,

        var comment: String?,

        var documentNumber: String?,

        var documentYear: Int?,

        var issueCity: String?,

        var issueCountry: String?,

        var issueDate: Date?,

        var issueInstitution: String?,

        var issueInstitutionUsosCode: String?,

        var modificationDate: String?

) : Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Document

                if (id != other.id) return false
                if (certificateType != other.certificateType) return false
                if (certificateTypeCode != other.certificateTypeCode) return false
                if (certificateUsosCode != other.certificateUsosCode) return false
                if (comment != other.comment) return false
                if (documentNumber != other.documentNumber) return false
                if (documentYear != other.documentYear) return false
                if (issueCity != other.issueCity) return false
                if (issueCountry != other.issueCountry) return false
                if (issueDate != other.issueDate) return false
                if (issueInstitution != other.issueInstitution) return false
                if (issueInstitutionUsosCode != other.issueInstitutionUsosCode) return false
                if (modificationDate != other.modificationDate) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                result = 31 * result + certificateType.hashCode()
                result = 31 * result + certificateTypeCode.hashCode()
                result = 31 * result + certificateUsosCode.hashCode()
                result = 31 * result + (comment?.hashCode() ?: 0)
                result = 31 * result + (documentNumber?.hashCode() ?: 0)
                result = 31 * result + (documentYear ?: 0)
                result = 31 * result + (issueCity?.hashCode() ?: 0)
                result = 31 * result + (issueCountry?.hashCode() ?: 0)
                result = 31 * result + (issueDate?.hashCode() ?: 0)
                result = 31 * result + (issueInstitution?.hashCode() ?: 0)
                result = 31 * result + (issueInstitutionUsosCode?.hashCode() ?: 0)
                result = 31 * result + (modificationDate?.hashCode() ?: 0)
                return result
        }

        override fun toString(): String {
                return "Document(id=$id, certificateType='$certificateType', certificateTypeCode='$certificateTypeCode', certificateUsosCode=$certificateUsosCode, comment=$comment, documentNumber=$documentNumber, documentYear=$documentYear, issueCity=$issueCity, issueCountry=$issueCountry, issueDate=$issueDate, issueInstitution=$issueInstitution, issueInstitutionUsosCode=$issueInstitutionUsosCode, modificationDate=$modificationDate)"
        }


}