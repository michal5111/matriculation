package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_KIERUNKI_STUDIOW")
data class FieldOfStudy(

        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        val description: String,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "NADRZEDNY_KOD", referencedColumnName = "KOD", nullable = false)
        var fieldOfStudy: FieldOfStudy? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYP_KIERUNKU_KOD", referencedColumnName = "KOD", nullable = false)
        var fieldOfStudyType: FieldOfStudyType,

        @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
        var display: Char = 'T',

        @Column(name = "DESCRIPTION", length = 200, nullable = false)
        var descriptionEng: String? = null,

        @Column(name = "OPIS_NIE", length = 200, nullable = true)
        var descriptionGer: String? = null,

        @Column(name = "OPIS_ROS", length = 200, nullable = true)
        var descriptionRus: String? = null,

        @Column(name = "OPIS_HIS", length = 200, nullable = true)
        var descriptionHis: String? = null,

        @Column(name = "OPIS_FRA", length = 200, nullable = true)
        var descriptionFra: String? = null,

        @Column(name = "KOD_POLON", length = 20, nullable = false)
        var polonCode: String,

        @OneToMany(mappedBy = "fieldOfStudy", fetch = FetchType.LAZY)
        var conductedFieldOfStudy: MutableList<ConductedFieldOfStudy>,

        @OneToMany(mappedBy = "fieldOfStudySpeciality", fetch = FetchType.LAZY)
        var conductedFieldOfStudySpeciality: MutableList<ConductedFieldOfStudy>
)