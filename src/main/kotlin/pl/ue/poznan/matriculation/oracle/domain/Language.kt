package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_JEZYKI")
data class Language(
        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "NAZWA", length = 30, nullable = false)
        var name: String,

        @Column(name = "NAZWA_ANG", length = 100, nullable = false)
        var nameEng: String,

        @Column(name = "KOD_ISO6391", length = 2, nullable = true)
        var Iso6391Code: String? = name,

        @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
        var conductedFieldsOfStudy: MutableList<ConductedFieldOfStudy>
)