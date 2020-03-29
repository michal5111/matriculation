package pl.ue.poznan.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
data class EducationData(

        @Id
        var applicantId: Long? = null,

        @MapsId
        @JsonIgnore
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        @OneToMany(mappedBy = "educationData", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        var documents: MutableList<Document>,

        var highSchoolCity: String?,

        var highSchoolName: String?,

        var highSchoolType: String?,

        var highSchoolUsosCode: Long?
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as EducationData

                if (applicantId != other.applicantId) return false
                if (documents != other.documents) return false
                if (highSchoolCity != other.highSchoolCity) return false
                if (highSchoolName != other.highSchoolName) return false
                if (highSchoolType != other.highSchoolType) return false
                if (highSchoolUsosCode != other.highSchoolUsosCode) return false

                return true
        }

        override fun hashCode(): Int {
                var result = applicantId?.hashCode() ?: 0
                result = 31 * result + documents.hashCode()
                result = 31 * result + (highSchoolCity?.hashCode() ?: 0)
                result = 31 * result + (highSchoolName?.hashCode() ?: 0)
                result = 31 * result + (highSchoolType?.hashCode() ?: 0)
                result = 31 * result + (highSchoolUsosCode?.hashCode() ?: 0)
                return result
        }
}