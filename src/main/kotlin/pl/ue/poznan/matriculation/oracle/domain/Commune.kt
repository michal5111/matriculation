package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_GMINY")
data class Commune(
        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "NAZWA", length = 100, nullable = false)
        var name: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PW_KOD", referencedColumnName = "KOD", nullable = false)
        var county: County,

//        @Column(name = "UTW_ID", nullable = false)
//        val creatorOracleUser: String? = null,
//
//        @Column(name = "UTW_DATA", nullable = false)
//        val creationDate: Date? = null,
//
//        @Column(name = "MOD_ID", nullable = false)
//        val modificationOracleUser: String? = null,
//
//        @Column(name = "MOD_DATA", nullable = false)
//        val modificationDate: Date? = null,

        @OneToOne(mappedBy = "commune")
        var address: Address
)