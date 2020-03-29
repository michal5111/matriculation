package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_TYPY_PUNKTOW")
data class PointType(
        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 100, nullable = true)
        var description: String,

        @Column(name = "DESCRIPTION", length = 100, nullable = true)
        var descriptionEng: String,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "pointType")
        var mediumConfigurations: MutableList<MediumConfiguration>
)