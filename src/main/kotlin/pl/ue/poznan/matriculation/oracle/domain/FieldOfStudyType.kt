package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_TYPY_KIERUNKOW")
data class FieldOfStudyType(
        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 100, nullable = false)
        var description: String,

        @Column(name = "DESCRIPTION", length = 100, nullable = false)
        var descriptionEng: String,

        @Column(name = "OPIS_NIE", length = 100, nullable = true)
        var descriptionGer: String? = null,

        @Column(name = "OPIS_ROS", length = 100, nullable = true)
        var descriptionRus: String? = null,

        @Column(name = "OPIS_HIS", length = 100, nullable = true)
        var descriptionHis: String? = null,

        @Column(name = "OPIS_FRA", length = 100, nullable = true)
        var descriptionFra: String? = null,

        @OneToMany(mappedBy = "fieldOfStudyType", fetch = FetchType.LAZY)
        var fieldsOfStudy: MutableList<FieldOfStudy>
)