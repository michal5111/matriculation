package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.enum.AccommodationPreference
import pl.poznan.ue.matriculation.local.domain.enum.DurationType
import java.io.Serializable
import javax.persistence.*

@Entity
class ErasmusData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        @Enumerated(EnumType.STRING)
        var accommodationPreference: AccommodationPreference?,

        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "home_institution_id", referencedColumnName = "id")
        var homeInstitution: HomeInstitution?,

        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "coordinator_data_id", referencedColumnName = "id")
        var coordinatorData: CoordinatorData?,

        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "course_id", referencedColumnName = "id")
        var courseData: CourseData?,

        var type: String?,

        var duration: DurationType?
) : BaseEntity(), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ErasmusData

        if (applicant != other.applicant) return false
        if (accommodationPreference != other.accommodationPreference) return false
        if (homeInstitution != other.homeInstitution) return false
        if (coordinatorData != other.coordinatorData) return false
        if (courseData != other.courseData) return false
        if (type != other.type) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = applicant?.hashCode() ?: 0
        result = 31 * result + (accommodationPreference?.hashCode() ?: 0)
        result = 31 * result + (homeInstitution?.hashCode() ?: 0)
        result = 31 * result + (coordinatorData?.hashCode() ?: 0)
        result = 31 * result + (courseData?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (duration?.hashCode() ?: 0)
        return result
    }
}
