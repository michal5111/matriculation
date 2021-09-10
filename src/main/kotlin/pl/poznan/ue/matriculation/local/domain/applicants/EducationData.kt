package pl.poznan.ue.matriculation.local.domain.applicants

import pl.poznan.ue.matriculation.local.domain.BaseEntityApplicantId
import java.io.Serializable
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
class EducationData(

    @OneToMany(mappedBy = "educationData", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val documents: MutableSet<Document> = HashSet(),

    var highSchoolCity: String? = null,

    var highSchoolName: String? = null,

    var highSchoolType: String? = null,

    var highSchoolUsosCode: Long? = null,

    applicant: Applicant? = null
) : BaseEntityApplicantId(applicant), Serializable {

    override fun toString(): String {
        return "EducationData(applicantId=$applicantId, highSchoolCity=$highSchoolCity, " +
            "highSchoolName=$highSchoolName, highSchoolType=$highSchoolType, " +
            "highSchoolUsosCode=$highSchoolUsosCode)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EducationData

        if (applicantId != other.applicantId) return false

        return true
    }

    override fun hashCode(): Int {
        return applicantId?.hashCode() ?: 0
    }

    fun addDocument(document: Document) {
        documents.add(document)
        document.educationData = this
    }

    fun removeDocument(document: Document) {
        documents.remove(document)
        document.educationData = null
    }

}
