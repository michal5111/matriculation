package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_URZEDY_SKARBOWE")
class TaxOffice(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_US_SEQ")
        @SequenceGenerator(sequenceName = "DZ_US_SEQ", allocationSize = 1, name = "DZ_US_SEQ")
        @Column(name = "ID")
        val id: Long? = null,

        @Column(name = "NAZWA", length = 100, nullable = false)
        val name: String,

        @Column(name = "KOD", length = 4, nullable = false)
        val code: String,

//        @Column(name = "MOD_DATA", nullable = false)
//        val modificationDate: Date,
//
//        @Column(name = "MOD_ID", length = 30, nullable = false)
//        val modificationUser: String,
//
//        @Column(name = "UTW_DATA", nullable = false)
//        val creationDate: Date,
//
//        @Column(name = "UTW_ID", length = 30, nullable = false)
//        val creationUser: String,

        @OneToMany(mappedBy = "taxOffice", fetch = FetchType.LAZY)
        val persons: Set<Person>,

        @OneToOne(mappedBy = "taxOffice", fetch = FetchType.LAZY)
        var address: Address

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaxOffice

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}