package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_OSOBY")
class Person(
        @Id
        @GeneratedValue
        @Column(name = "ID")
        val id: Long,

        @Column(name = "PESEL")
        val pesel: String?,

        @Column(name = "IMIE")
        val name: String,

        @Column(name = "IMIE2")
        val secondName: String?,

        @Column(name = "NAZWISKO")
        val surname: String,

        @Column(name = "DATA_UR")
        val birthDate: Date?,

        @Column(name = "MIASTO_UR")
        val birthCity: String?,

        @Column(name = "IMIE_OJCA")
        val fathersName: String?,

        @Column(name = "IMIE_MATKI")
        val mothersName: String?,

        @Column(name ="NAZWISKO_PANIEN_MATKI")
        val mothersMaidenName: String?,

        @Column(name = "NAZWISKO_RODOWE")
        val familyName: String?,

        @Column(name = "SZKOLA")
        val school: String?,

        @Column(name = "SR_NR_DOWODU")
        val idNumber: String?,

        @Column(name = "NIP")
        val nip: String?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "WKU_KOD", referencedColumnName = "KOD")
        val wku: Wku,

        @Column(name = "KAT_WOJSKOWA")
        val militaryCategory: String?,

        @Column(name = "STOSUNEK_WOJSKOWY")
        val militaryRelationship: String?,

        @Column(name = "UWAGI")
        val comments: String?,

        @Column(name = "WWW")
        val www: String?,

        @Column(name = "EMAIL")
        val email: String?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "NAR_KOD", referencedColumnName = "KOD")
        val nationality: Citizenship,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OB_KOD", referencedColumnName = "KOD")
        val citizenship: Citizenship,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
        val organizationalUnit: OrganizationalUnit,

        @Column(name = "UTW_ID")
        val creatorOracleUser: String,

        @Column(name = "UTW_DATA")
        val creationDate: Date,

        @Column(name = "MOD_ID")
        val ModificationOracleUser: String,

        @Column(name = "MOD_DATA")
        val modificationDate: Date,

        @Column(name = "PLEC")
        val sex: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYTUL_PRZED")
        val titlePrefix: Title?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYTUL_PO")
        val titleSuffix: Title?,

        @Column(name = "CZY_POLONIA")
        val isPolish: String?,

        @Column(name = "ZAMIEJSCOWA")
        val nonresident: String?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "GDZIE_SOCJALNE", referencedColumnName = "KOD")
        val socialBenefitsSource: OrganizationalUnit?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "US_ID", referencedColumnName = "ID")
        val taxOffice: TaxOffice,

        @Column(name = "AKAD_CZY_REZERWA")
        val dormitoryReserve: String?,

        @Column(name = "AKAD_WYKROCZENIA")
        val dormitoryOffense: String?,

        @Column(name = "AKAD_UWAGI")
        val dormitoryComments: String?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SZK_ID", referencedColumnName = "ID")
        val middleSchool: School?,

        @Column(name = "TYP_DOKUMENTU")
        val documentType: String?,

        @Column(name = "NR_KARTY_BIBL")
        val libraryCardNumber: String,

        @Column(name = "BK_EMAIL")
        val carrersOfficeEmail: String?,

        @Column(name = "BK_CZYMIGROWAC")
        val carrersOfficeMigrate: Int?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRAJ_URODZENIA", referencedColumnName = "KOD")
        val birthCountry: Citizenship?,

        @Column(name = "DANE_ZEW_STATUS")
        val externalDataStatus: String,

        @Column(name = "OSIAGNIECIA")
        val achievements: String?,

        @Column(name = "OSIAGNIECIA_ANG")
        val achievementsEng: String?,

        @Column(name = "EPUAP_IDENTYFIKATOR")
        val epuapId: String?,

        @Column(name = "EPUAP_SKRYTKA")
        val epuapSafe: String?,

        @Column(name = "CZY_LOSY_ABSOLWENTOW")
        val isGraduateFate: String,

        @Column(name = "CZY_LOSY_ABS_ZGODA")
        val isGraduateFateDate: Date?,

        @Column(name = "CZY_LOSY_ABS_REZYG")
        val isGraduateFateResignDate: Date?,

        @Column(name = "BK_CZY_MIGR_ZGODA")
        val carrersOfficeMigrateApproval: Date?,

        @Column(name = "BK_CZY_MIGR_REZYG")
        val carrersOfficeMigrateResignation: Date?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRAJ_DOK_KOD", referencedColumnName = "KOD")
        val identityDocumentIssuerCountry: Citizenship?,

        @Column(name = "DATA_WAZNOSCI_DOWODU")
        val identityDocumentExpirationDate: Date?,

        @Column(name = "CZY_KLUB_ABS")
        val graduateClubJoinApproval: String?,

        @Column(name = "CZY_KLUB_ABS_ZGODA")
        val graduateClubJoinApprovalDate: String?,

        @Column(name = "CZY_KLUB_ABS_REZYG")
        val graduateClubJoinResignDate: String?
        )