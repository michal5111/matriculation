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
) : Serializable
