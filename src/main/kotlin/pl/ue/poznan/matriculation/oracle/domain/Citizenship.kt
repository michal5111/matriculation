package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_OBYWATELSTWA")
data class Citizenship(
        @Id
        @Column(name = "KOD")
        val code: String,

        @Column(name = "OBYWATELSTWO")
        val nationalityString: String?,

        @Column(name = "KRAJ")
        val country: String?,

        @Column(name = "KONTYNENT")
        val continent: String?,

        @Column(name = "ISOKOD")
        val isoCode: String?,

        @Column(name = "KRAJ_ANG")
        val countryEng: String?,

        @Column(name = "MOD_DATA")
        val modificationDate: Date,

        @Column(name = "MOD_ID")
        val modificationUser: String,

        @Column(name = "UTW_DATA")
        val creationDate: Date,

        @Column(name = "UTW_ID")
        val creationUser: String,

        @Column(name = "CZY_UE")
        val isEU: String,

        @Column(name = "CZY_EFTA")
        val isEFTA: String,

        @Column(name = "KRAJ_POLON")
        val polonCountry: String?,

        @JsonIgnore
        @OneToMany(mappedBy = "nationality", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val personsNationality: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "citizenship", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val personsCitizenship: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "birthCountry", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val personsBirthCountry: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "citizenship", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val nutsRegions: Set<NUTSRegion>,

        @JsonIgnore
        @OneToMany(mappedBy = "identityDocumentIssuerCountry", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val identityDocumentIssuerCountryPersons: Set<Person>
)