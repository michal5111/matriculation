package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_OBYWATELSTWA")
class Citizenship(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OBYWATELSTWO", length = 100, nullable = true)
        val nationalityString: String?,

        @Column(name = "KRAJ", length = 100, nullable = true)
        val country: String?,

        @Column(name = "KONTYNENT", length = 30, nullable = true)
        val continent: String?,

        @Column(name = "ISOKOD", length = 2, nullable = true)
        val isoCode: String?,

        @Column(name = "KRAJ_ANG", length = 100, nullable = true)
        val countryEng: String?,

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

        @Column(name = "CZY_UE", length = 1, nullable = false)
        val isEU: String,

        @Column(name = "CZY_EFTA", length = 1, nullable = false)
        val isEFTA: String,

        @Column(name = "KRAJ_POLON", length = 100, nullable = false)
        val polonCountry: String?,

        @JsonIgnore
        @OneToMany(mappedBy = "nationality", fetch = FetchType.LAZY)
        val personsNationality: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "citizenship", fetch = FetchType.LAZY)
        val personsCitizenship: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "birthCountry", fetch = FetchType.LAZY)
        val personsBirthCountry: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "citizenship", fetch = FetchType.LAZY)
        val nutsRegions: Set<NUTSRegion>,

        @JsonIgnore
        @OneToMany(mappedBy = "identityDocumentIssuerCountry", fetch = FetchType.LAZY)
        val identityDocumentIssuerCountryPersons: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "countryCode", fetch = FetchType.LAZY)
        var address: List<Address>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Citizenship

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}