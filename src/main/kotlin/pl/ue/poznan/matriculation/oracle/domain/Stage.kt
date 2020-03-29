package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_ETAPY")
data class Stage(

        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        val description: String,

        @Column(name = "DESCRIPTION", length = 200, nullable = true)
        val descriptionEng: String? = null,

        @OneToMany(mappedBy = "stage", fetch = FetchType.LAZY)
        val programmeStages: MutableList<ProgrammeStage>
)