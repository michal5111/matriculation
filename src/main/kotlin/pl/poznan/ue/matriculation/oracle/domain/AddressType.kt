package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "DZ_TYPY_ADRESOW")
data class AddressType(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 100, nullable = false)
        var description: String

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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddressType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

}