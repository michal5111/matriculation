package pl.poznan.ue.matriculation.cem.domain

import org.hibernate.annotations.Immutable
import pl.poznan.ue.matriculation.cem.enum.ApplicationStatus
import pl.poznan.ue.matriculation.local.dto.IApplicationDto
import javax.persistence.*

@Entity
@Immutable
@Table(name = "applications")
open class CemApplication : IApplicationDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    var cemStudent: CemStudent? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_edition_id", referencedColumnName = "id")
    var courseEdition: CourseEdition? = null

    @Enumerated(EnumType.ORDINAL)
    val status: ApplicationStatus = ApplicationStatus.NEW

    override val foreignApplicantId: Long
        get() = cemStudent!!.foreignId

    override val foreignId: Long
        get() = id!!
}
