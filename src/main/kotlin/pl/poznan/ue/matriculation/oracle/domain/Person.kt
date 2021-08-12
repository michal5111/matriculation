package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "DZ_OSOBY")
class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_OS_SEQ")
    @SequenceGenerator(sequenceName = "DZ_OS_SEQ", allocationSize = 1, name = "DZ_OS_SEQ")
    @Column(name = "ID", nullable = false, updatable = false, length = 10)
    val id: Long? = null,

    @Column(name = "PESEL", length = 11, nullable = true)
    var pesel: String? = null,

    @Column(name = "IMIE", length = 40, nullable = false)
    var name: String,

    @Column(name = "IMIE2", length = 40, nullable = true)
    var middleName: String? = null,

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

    @Column(name = "NAZWISKO_PANIEN_MATKI", length = 40, nullable = true)
    val mothersMaidenName: String? = null,

    @Column(name = "NAZWISKO_RODOWE", length = 40, nullable = true)
    val familyName: String? = null,

    @Column(name = "SZKOLA", length = 200, nullable = true)
    val school: String? = null,

    @Column(name = "SR_NR_DOWODU", length = 20, nullable = true)
    var idNumber: String? = null,

    @Column(name = "NIP", length = 13, nullable = true)
    val nip: String? = null,

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

    @Column(name = "EMAIL")
    var email: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NAR_KOD", referencedColumnName = "KOD")
    var nationality: Citizenship? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OB_KOD", referencedColumnName = "KOD")
    var citizenship: Citizenship? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
    var organizationalUnit: OrganizationalUnit,

    @Column(name = "PLEC", nullable = false, length = 1)
    var sex: Char,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYTUL_PRZED", nullable = true)
    val titlePrefix: Title? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYTUL_PO", nullable = true)
    val titleSuffix: Title? = null,

    @Column(name = "CZY_POLONIA", length = 1, nullable = true)
    val isPolish: Char? = 'N',

    @Column(name = "ZAMIEJSCOWA", length = 1, nullable = true)
    val nonresident: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GDZIE_SOCJALNE", referencedColumnName = "KOD", nullable = true)
    val socialBenefitsSource: OrganizationalUnit? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "US_ID", referencedColumnName = "ID", nullable = true)
    val taxOffice: TaxOffice? = null,

    @Column(name = "AKAD_CZY_REZERWA", length = 1, nullable = false)
    val dormitoryReserve: Char = 'N',

    @Column(name = "AKAD_WYKROCZENIA", length = 1000, nullable = true)
    val dormitoryOffense: String? = null,

    @Column(name = "AKAD_UWAGI", length = 1000, nullable = true)
    val dormitoryComments: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
    var middleSchool: School? = null,

    @Column(name = "TYP_DOKUMENTU", length = 1, nullable = true)
    var documentType: Char? = null,

    @Column(name = "NR_KARTY_BIBL", length = 30, nullable = true)
    val libraryCardNumber: String? = null,

    @Column(name = "BK_EMAIL", length = 100, nullable = true)
    var privateEmail: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KRAJ_URODZENIA", referencedColumnName = "KOD", nullable = true)
    var birthCountry: Citizenship? = null,

    @Column(name = "DANE_ZEW_STATUS", length = 1, nullable = false)
    var externalDataStatus: Char = 'U',

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KRAJ_DOK_KOD", referencedColumnName = "KOD", nullable = true)
    var identityDocumentIssuerCountry: Citizenship? = null,

    @Column(name = "DATA_WAZNOSCI_DOWODU", nullable = true)
    var identityDocumentExpirationDate: Date? = null,

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var addresses: MutableList<Address>,

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var phoneNumbers: MutableList<PhoneNumber>,

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var entitlementDocuments: MutableList<EntitlementDocument>,

    @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var personPhoto: PersonPhoto? = null,

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var personPreferences: MutableList<PersonPreference> = mutableListOf(),

    @OneToMany(mappedBy = "person", orphanRemoval = true, fetch = FetchType.LAZY)
    var student: MutableList<Student> = mutableListOf(),

    @OneToMany(mappedBy = "person", orphanRemoval = true, fetch = FetchType.LAZY)
    var personProgrammes: MutableList<PersonProgramme> = mutableListOf(),

    @OneToMany(mappedBy = "person", orphanRemoval = true, fetch = FetchType.LAZY)
    var folders: MutableList<Folder> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var documents: MutableList<PersonDocument> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var ownedDocuments: MutableList<OwnedDocument> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var personChangeHistory: MutableList<PersonChangeHistory> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var personArrivals: MutableList<Arrival> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var personContracts: MutableList<Contract> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var personEmployee: MutableList<Employee> = mutableListOf()
) : BaseEntity() {

    override fun toString(): String {
        return "Person(id=$id, pesel=$pesel, name='$name', middleName=$middleName, surname='$surname', birthDate=$birthDate, birthCity=$birthCity, fathersName=$fathersName, mothersName=$mothersName, mothersMaidenName=$mothersMaidenName, familyName=$familyName, school=$school, idNumber=$idNumber, nip=$nip, email=$email, sex=$sex, privateEmail=$privateEmail)"
    }


    @PostUpdate
    fun postUpdate() {
        println("Modification date post: $modificationDate")
    }
}