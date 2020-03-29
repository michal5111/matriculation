package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import org.hibernate.validator.constraints.pl.PESEL
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

//@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_OSOBY")
class Person(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_OS_SEQ")
        @SequenceGenerator(sequenceName = "DZ_OS_SEQ", allocationSize = 1, name = "DZ_OS_SEQ")
        @Column(name = "ID", nullable = false, updatable = false, length = 10)
        val id: Long? = null,

        @PESEL
        @Column(name = "PESEL", length = 11, nullable = true)
        val pesel: String? = null,

        @NotBlank
        @Column(name = "IMIE", length = 40, nullable = false)
        var name: String,

        @Column(name = "IMIE2", length = 40, nullable = true)
        var middleName: String? = null,

        @NotBlank
        @Column(name = "NAZWISKO", length = 40, nullable = false)
        var surname: String,

        @Column(name = "DATA_UR", length = 60, nullable = true)
        var birthDate: Date? = null,

        @Column(name = "MIASTO_UR", length = 60, nullable = true)
        var birthCity: String? = null,

        @Column(name = "IMIE_OJCA", length = 40, nullable = true)
        var fathersName: String? = null,

        @Column(name = "IMIE_MATKI", length = 40, nullable = true)
        var mothersName: String? = null,

        @Column(name ="NAZWISKO_PANIEN_MATKI", length = 40, nullable = true)
        val mothersMaidenName: String? = null,

        @Column(name = "NAZWISKO_RODOWE", length = 40, nullable = true)
        val familyName: String? = null,

        @Column(name = "SZKOLA", length = 200, nullable = true)
        val school: String? = null,

        @Column(name = "SR_NR_DOWODU", length = 20, nullable = true)
        val idNumber: String? = null,

        @Column(name = "NIP", length = 13, nullable = true)
        val nip: String? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "WKU_KOD", referencedColumnName = "KOD", nullable = true)
        var wku: Wku? = null,

        @Column(name = "KAT_WOJSKOWA")
        var militaryCategory: String? = null,

        @Column(name = "STOSUNEK_WOJSKOWY")
        var militaryStatus: String? = null,

        @Column(name = "UWAGI")
        val comments: String? = null,

        @Column(name = "WWW")
        val www: String? = null,

        @Email
        @Column(name = "EMAIL")
        var email: String? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "NAR_KOD", referencedColumnName = "KOD")
        var nationality: Citizenship?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OB_KOD", referencedColumnName = "KOD")
        var citizenship: Citizenship?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
        var organizationalUnit: OrganizationalUnit,

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

        @Column(name = "PLEC", nullable = false, length = 1)
        var sex: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYTUL_PRZED", nullable = true)
        val titlePrefix: Title? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYTUL_PO", nullable = true)
        val titleSuffix: Title? = null,

        @Column(name = "CZY_POLONIA", length = 1, nullable = true)
        val isPolish: Char = 'N',

        @Column(name = "ZAMIEJSCOWA", length = 1, nullable = true)
        val nonresident: String? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "GDZIE_SOCJALNE", referencedColumnName = "KOD", nullable = true)
        val socialBenefitsSource: OrganizationalUnit? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "US_ID", referencedColumnName = "ID", nullable = true)
        val taxOffice: TaxOffice? = null,

        @Column(name = "AKAD_CZY_REZERWA", length = 1, nullable = false)
        val dormitoryReserve: Char = 'N',

        @Column(name = "AKAD_WYKROCZENIA", length = 1000, nullable = true)
        val dormitoryOffense: String? = null,

        @Column(name = "AKAD_UWAGI", length = 1000, nullable = true)
        val dormitoryComments: String? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
        var middleSchool: School? = null,

        @Column(name = "TYP_DOKUMENTU", length = 1, nullable = true)
        val documentType: String? = null,

        @Column(name = "NR_KARTY_BIBL", length = 30, nullable = true)
        val libraryCardNumber: String? = null,

        @Email
        @Column(name = "BK_EMAIL", length = 100, nullable = true)
        val careersOfficeEmail: String? = null,

        @Column(name = "BK_CZYMIGROWAC", length = 2, precision = 0, nullable = false)
        val careersOfficeMigrate: Int = 0,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRAJ_URODZENIA", referencedColumnName = "KOD", nullable = true)
        var birthCountry: Citizenship? = null,

        @Column(name = "DANE_ZEW_STATUS", length = 1, nullable = false)
        val externalDataStatus: Char = 'U',

        @Lob
        @Column(name = "OSIAGNIECIA", nullable = true)
        val achievements: String? = null,

        @Lob
        @Column(name = "OSIAGNIECIA_ANG", nullable = true)
        val achievementsEng: String? = null,

        @Column(name = "EPUAP_IDENTYFIKATOR", length = 64, nullable = true)
        val epuapId: String? = null,

        @Column(name = "EPUAP_SKRYTKA", length = 64, nullable = true)
        val epuapSafe: String? = null,

        @Column(name = "CZY_LOSY_ABSOLWENTOW", length = 1, nullable = false)
        val isGraduateFate: Char = 'N',

        @Column(name = "CZY_LOSY_ABS_ZGODA", nullable = true)
        val isGraduateFateDate: Date? = null,

        @Column(name = "CZY_LOSY_ABS_REZYG", nullable = true)
        val isGraduateFateResignDate: Date? = null,

        @Column(name = "BK_CZY_MIGR_ZGODA", nullable = true)
        val careersOfficeMigrateApproval: Date? = null,

        @Column(name = "BK_CZY_MIGR_REZYG", nullable = true)
        val careersOfficeMigrateResignation: Date? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRAJ_DOK_KOD", referencedColumnName = "KOD", nullable = true)
        val identityDocumentIssuerCountry: Citizenship? = null,

        @Column(name = "DATA_WAZNOSCI_DOWODU", nullable = true)
        val identityDocumentExpirationDate: Date? = null,

        @Column(name = "CZY_KLUB_ABS", length = 1, nullable = false)
        val graduateClubJoinApproval: Char = 'N',

        @Column(name = "CZY_KLUB_ABS_ZGODA", nullable = true)
        val graduateClubJoinApprovalDate: Date? = null,

        @Column(name = "CZY_KLUB_ABS_REZYG", nullable = true)
        val graduateClubJoinResignDate: Date? = null,

        //@JsonIgnore
        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        var addresses: MutableList<Address>,

        //@JsonIgnore
        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        var phoneNumbers: MutableList<PhoneNumber>,

        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        var entitlementDocuments: MutableList<EntitlementDocument>,

        @JsonIgnore
        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        var personPhoto: PersonPhoto? = null,

        @JsonIgnore
        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        var personPreferences: MutableList<PersonPreference> = mutableListOf(),

        @JsonIgnore
        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        var student: MutableList<Student> = mutableListOf(),

        @JsonIgnore
        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        var personProgrammes: MutableList<PersonProgramme> = mutableListOf(),

        @JsonIgnore
        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        var folders: MutableList<Folder> = mutableListOf()
)