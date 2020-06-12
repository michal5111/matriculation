package pl.poznan.ue.matriculation.local.domain.applicants


import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CacheConcurrencyStrategy
import pl.poznan.ue.matriculation.local.domain.applications.Application
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Document(

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

        var modificationDate: String?,

        @get:JsonIgnore
        @OneToMany(mappedBy = "certificate", fetch = FetchType.LAZY)
        var Applications: MutableList<Application> = mutableListOf()

) : Serializable {

    override fun toString(): String {
        return "Document(id=$id, certificateType='$certificateType', certificateTypeCode='$certificateTypeCode" +
                "', certificateUsosCode=$certificateUsosCode, comment=$comment, documentNumber=$documentNumber, " +
                "documentYear=$documentYear, issueCity=$issueCity, issueCountry=$issueCountry, " +
                "issueDate=$issueDate, issueInstitution=$issueInstitution, " +
                "issueInstitutionUsosCode=$issueInstitutionUsosCode, modificationDate=$modificationDate)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        if (id != other.id) return false

        return true
        }

        override fun hashCode(): Int {
            return id?.hashCode() ?: 0
        }


}