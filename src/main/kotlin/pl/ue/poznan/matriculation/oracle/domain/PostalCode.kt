package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_KODY_POCZTOWE")
data class PostalCode(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_KOD_POCZ_SEQ")
        @SequenceGenerator(sequenceName = "DZ_KOD_POCZ_SEQ", allocationSize = 1, name = "DZ_KOD_POCZ_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @NotBlank
        @Column(name = "KOD", length = 5, nullable = false)
        val code: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PW_KOD", referencedColumnName = "KOD", nullable = true)
        var county: County? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "GM_KOD", referencedColumnName = "KOD", nullable = true)
        var commune: Commune? = null,

        @Column(name = "POCZTA", length = 100, nullable = true)
        var post: String,

        @Column(name = "CZY_MIASTO", length = 1, nullable = true)
        var cityIsCity: Char?
)