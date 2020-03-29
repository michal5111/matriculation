package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_PODSTAWY_PODJECIA_STUDIOW")
data class GroundsForUndertakingStudies(
        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        val description: String,

        @Column(name = "OPIS_ANG", length = 200, nullable = true)
        val descriptionEng: String?,

        @Column(name = "CZY_AKTUALNA", length = 1, nullable = false)
        val isCurrent: Char = 'T',

        @OneToMany(mappedBy = "groundsForUndertakingStudies", fetch = FetchType.LAZY)
        val personProgrammes: MutableList<PersonProgramme>
)