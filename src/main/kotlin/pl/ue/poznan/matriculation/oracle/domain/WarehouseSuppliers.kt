package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_MAG_DOSTAWCY")
data class WarehouseSuppliers(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_MAG_DOST_SEQ")
        @SequenceGenerator(sequenceName = "DZ_MAG_DOST_SEQ", allocationSize = 1, name = "DZ_MAG_DOST_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @Column(name = "NAZWA_SKROCONA", length = 40, nullable = false)
        var shortName: String,

        @Column(name = "NAZWA_PELNA", length = 255, nullable = true)
        var fullName: String? = null,

        @Column(name = "NIP", length = 13, nullable = true)
        var nip: String? = null,

        @Column(name = "KOMENTARZ", length = 2000, nullable = true)
        var comment: String? = null

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
//        val modificationDate: Date? = null
)