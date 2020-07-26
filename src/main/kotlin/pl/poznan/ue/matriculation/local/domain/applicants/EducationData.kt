package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
class EducationData(

        @Id
        @JsonIgnore
        var applicantId: Long? = null,

        @MapsId
        @JsonIgnore
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        @OneToMany(mappedBy = "educationData", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
        var documents: MutableList<Document>,

        var highSchoolCity: String?,

        var highSchoolName: String?,

        var highSchoolType: String?,

        var highSchoolUsosCode: Long?
) : Serializable {

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


}