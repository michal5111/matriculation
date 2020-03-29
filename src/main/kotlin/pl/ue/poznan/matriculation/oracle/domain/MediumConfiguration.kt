package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_KONFIG_SREDNICH")
data class MediumConfiguration(
        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 1000, nullable = false)
        var description: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TPKT_KOD", referencedColumnName = "KOD")
        var pointType: PointType? = null,

        @Column(name = "CZY_UWZGLEDNIA_NZAL", length = 1, nullable = false)
        var includeNonCreditable: Char,

        @Column(name = "DOKLADNOSC", length = 1, nullable = false)
        var accuracy: Int,

        @Column(name = "SPOSOB_ZAOKRAGLENIA", length = 1, nullable = false)
        var roundWay: Char,

        @Column(name = "DOMYSLNY_STATUS_PODP", length = 1, nullable = false)
        var defaultHookStatus: Char,

        @OneToMany(mappedBy = "mediumConfiguration", fetch = FetchType.LAZY)
        var programmes: MutableList<Programme>
)