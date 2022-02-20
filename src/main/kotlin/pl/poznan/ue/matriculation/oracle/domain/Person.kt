package pl.poznan.ue.matriculation.oracle.domain

import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import java.util.*
import javax.persistence.*

@NamedEntityGraphs(
    NamedEntityGraph(
        name = "person.basicDataAndAddresses",
        attributeNodes = [
            NamedAttributeNode("nationality"),
            NamedAttributeNode("citizenship"),
            NamedAttributeNode("identityDocumentIssuerCountry"),
            NamedAttributeNode("addresses", subgraph = "subgraph.addressType"),
        ],
        subgraphs = [
            NamedSubgraph(name = "subgraph.addressType", attributeNodes = [NamedAttributeNode("addressType")])
        ]
    ),
    NamedEntityGraph(
        name = "person.phoneNumbers",
        attributeNodes = [NamedAttributeNode("phoneNumbers", subgraph = "subgraph.phoneNumberType")],
        subgraphs = [
            NamedSubgraph(name = "subgraph.phoneNumberType", attributeNodes = [NamedAttributeNode("phoneNumberType")])
        ]
    ),
    NamedEntityGraph(
        name = "person.entitlementDocuments",
        attributeNodes = [NamedAttributeNode("entitlementDocuments")]
    ),
    NamedEntityGraph(
        name = "person.personPreferences",
        attributeNodes = [NamedAttributeNode("personPreferences")]
    ),
    NamedEntityGraph(
        name = "person.student",
        attributeNodes = [NamedAttributeNode("students")]
    ),
    NamedEntityGraph(
        name = "person.personProgrammes",
        attributeNodes = [NamedAttributeNode("personProgrammes")]
    ),
    NamedEntityGraph(
        name = "person.ownedDocuments",
        attributeNodes = [NamedAttributeNode("ownedDocuments")]
    ),
    NamedEntityGraph(
        name = "person.personChangeHistory",
        attributeNodes = [NamedAttributeNode("personChangeHistories")]
    )
)

@Entity
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

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_POLONIA", length = 1, nullable = true)
    val isPolish: Boolean? = false,

    @Column(name = "ZAMIEJSCOWA", length = 1, nullable = true)
    val nonresident: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GDZIE_SOCJALNE", referencedColumnName = "KOD", nullable = true)
    val socialBenefitsSource: OrganizationalUnit? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "US_ID", referencedColumnName = "ID", nullable = true)
    val taxOffice: TaxOffice? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "AKAD_CZY_REZERWA", length = 1, nullable = false)
    val dormitoryReserve: Boolean = false,

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

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val addresses: MutableList<Address> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val phoneNumbers: MutableList<PhoneNumber> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val entitlementDocuments: MutableList<EntitlementDocument> = mutableListOf(),

    @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var personPhoto: PersonPhoto? = null,

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val personPreferences: MutableList<PersonPreference> = mutableListOf(),

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    val students: MutableList<Student> = mutableListOf(),

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    val personProgrammes: MutableList<PersonProgramme> = mutableListOf(),

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    val folders: MutableList<Folder> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val documents: MutableList<PersonDocument> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val ownedDocuments: MutableList<OwnedDocument> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val personChangeHistories: MutableList<PersonChangeHistory> = mutableListOf(),

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    val personArrivals: MutableList<Arrival> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val personContracts: MutableList<Contract> = mutableListOf(),

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val personEmployee: MutableList<Employee> = mutableListOf()
) : BaseEntity() {


    override fun toString(): String {
        return "Person(id=$id, pesel=$pesel, name='$name', middleName=$middleName, surname='$surname', birthDate=$birthDate, birthCity=$birthCity, fathersName=$fathersName, mothersName=$mothersName, mothersMaidenName=$mothersMaidenName, familyName=$familyName, school=$school, idNumber=$idNumber, nip=$nip, email=$email, sex=$sex, privateEmail=$privateEmail)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Person

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    fun addAddress(address: Address) {
        addresses.add(address)
        address.person = this
    }

    fun removeAddress(address: Address) {
        addresses.remove(address)
        address.person = null
    }

    fun addPhoneNumber(phoneNumber: PhoneNumber) {
        phoneNumbers.add(phoneNumber)
        phoneNumber.person = this
    }

    fun removePhoneNumber(phoneNumber: PhoneNumber) {
        phoneNumbers.remove(phoneNumber)
        phoneNumber.person = null
    }

    fun addEntitlementDocument(entitlementDocument: EntitlementDocument) {
        entitlementDocuments.add(entitlementDocument)
        entitlementDocument.person = this
    }

    fun removeEntitlementDocument(entitlementDocument: EntitlementDocument) {
        entitlementDocuments.remove(entitlementDocument)
        entitlementDocument.person = null
    }

    fun addPersonPreference(personPreference: PersonPreference) {
        personPreferences.add(personPreference)
        personPreference.person = this
    }

    fun addStudent(student: Student) {
        students.add(student)
        student.person = this
    }

    fun addOwnedDocument(ownedDocument: OwnedDocument) {
        ownedDocuments.add(ownedDocument)
        ownedDocument.person = this
    }

    fun addPersonChangeHistory(personChangeHistory: PersonChangeHistory) {
        personChangeHistories.add(personChangeHistory)
        personChangeHistory.person = this
    }

    fun addPersonArrivals(arrival: Arrival) {
        personArrivals.add(arrival)
        arrival.person = this
    }

    fun addPersonProgramme(personProgramme: PersonProgramme) {
        personProgrammes.add(personProgramme)
        personProgramme.person = this
    }
}
