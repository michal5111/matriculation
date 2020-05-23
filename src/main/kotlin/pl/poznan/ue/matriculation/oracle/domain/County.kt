package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "DZ_POWIATY")
data class County(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 100, nullable = false)
        var description: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "WJ_KOD", referencedColumnName = "KOD")
        var voivodeship: Voivodeship,

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

        @JsonIgnore
        @OneToMany(mappedBy = "county", fetch = FetchType.LAZY)
        var addresses: MutableList<Address>,

        @JsonIgnore
        @OneToMany(mappedBy = "county", fetch = FetchType.LAZY)
        var communes: List<Commune>,

        @JsonIgnore
        @OneToMany(mappedBy = "county", fetch = FetchType.LAZY)
        var postalCodes: MutableList<PostalCode>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as County

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}