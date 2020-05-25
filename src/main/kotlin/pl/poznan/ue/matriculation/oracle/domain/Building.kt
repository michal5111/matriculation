package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_BUDYNKI")
class Building(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "NAZWA", length = 100, nullable = false)
        var name: String,

        @Column(name = "SZEROKOSC_GEO", nullable = true)
        var latitude: Number? = null,

        @Column(name = "DLUGOSC_GEO", nullable = true)
        var longitude: Number? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KAMP_KOD", referencedColumnName = "KOD")
        var campus: Campus,

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

        @Column(name = "NAZWA_ANG", length = 100, nullable = true)
        var englishName: String? = null,

        @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
        var display: Char,

        @Column(name = "KOD_ERP", length = 50, nullable = true)
        var erpCode: String? = null,

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "FOTO_BLOB_ID", referencedColumnName = "ID")
        var photoBlob: Blob,

        @Column(name = "DATA_OD", nullable = true)
        var dateFrom: Date? = null,

        @Column(name = "DATA_DO", nullable = true)
        var dateTO: Date? = null,

        @JsonIgnore
        @OneToOne(mappedBy = "building")
        var address: Address,

        @JsonIgnore
        @OneToMany(mappedBy = "building")
        var phoneNumbers: List<PhoneNumber>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Building

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}