package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_KODY_ISCED")
class IscedCode(
        @Id
        @NotBlank
        @Column(name = "KOD", length = 5, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        var description: String,

        @Column(name = "OPIS_ANG", length = 200, nullable = false)
        var descriptionEng: String,

        @OneToMany(mappedBy = "iscedCode", fetch = FetchType.LAZY)
        val programmes: MutableList<Programme>,

        @OneToMany(mappedBy = "iscedCode", fetch = FetchType.LAZY)
        val personProgrammes: MutableList<PersonProgramme>
)