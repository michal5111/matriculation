package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "DZ_FIRMY")
class Company(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "NAZWA", length = 400, nullable = false)
        val name: String,

        @Column(name = "NAZWA_ANG", length = 400, nullable = true)
        var englishName: String? = null,

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

        @Column(name = "NIP", length = 13, nullable = true)
        var nip: String? = null,

        @Column(name = "MODUL", length = 10, nullable = true)
        var module: String? = null,

        @Column(name = "TYP_DZIALANOSCI", length = 1, nullable = true)
        var typeOfActivity: Char? = null,

        @Column(name = "FORMA_PRAWNA", length = 3, nullable = true)
        var legalForm: String? = null,

        @Column(name = "NUMER_KRS",length = 10, nullable = true)
        var krsNumber: String? = null,

        @Column(name = "REGON", length = 14, nullable = true)
        var regon: String? = null,

        @Column(name = "ADRES_WWW", length = 200, nullable = true)
        var website: String? = null,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "FRM_KOD", referencedColumnName = "KOD", nullable = true)
        var company: Company? = null,

        @JsonIgnore
        @OneToMany(mappedBy = "company")
        var phoneNumbers: List<PhoneNumber>

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Company

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}