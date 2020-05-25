package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "DZ_WKU")
class Wku(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "NAZWA", length = 100, nullable = false)
        val name: String,

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

        @JsonIgnore
        @OneToMany(mappedBy = "wku", fetch = FetchType.LAZY)
        val persons: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "wku", fetch = FetchType.LAZY)
        val addresses: List<Address>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Wku

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}