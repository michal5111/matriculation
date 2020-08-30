package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_KODY_SOKRATES")
class ErasmusCode(

        @Id
        @Column(name = "KOD", length = 5, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        val description: String,

        @Column(name = "OPIS_ANG", length = 200, nullable = true)
        val descriptionEng: String?,

        @Column(name = "KOD_ISCED", nullable = true, length = 5)
        var iscedCode: String,

        @OneToMany(mappedBy = "erasmusCode")
        var arrivals: List<Arrival>
)
