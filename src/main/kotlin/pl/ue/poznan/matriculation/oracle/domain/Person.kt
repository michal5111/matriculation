package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.hibernate.validator.constraints.pl.PESEL
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_OSOBY")
class Person(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_OS_SEQ")
        @SequenceGenerator(sequenceName = "DZ_OS_SEQ", allocationSize = 1, name = "DZ_OS_SEQ")
        @Column(name = "ID", nullable = false, updatable = false, length = 10)
        val id: Long,

        @PESEL
        @Column(name = "PESEL", length = 11, nullable = true)
        val pesel: String?,

        @NotBlank
        @Column(name = "IMIE", length = 40, nullable = false)
        val name: String,

        @Column(name = "IMIE2", length = 40, nullable = true)
        val secondName: String?,

        @NotBlank
        @Column(name = "NAZWISKO", length = 40, nullable = false)
        val surname: String,

        @Column(name = "DATA_UR", length = 60, nullable = true)
        val birthDate: Date?,

        @Column(name = "MIASTO_UR", length = 60, nullable = true)
        val birthCity: String?,

        @Column(name = "IMIE_OJCA", length = 40, nullable = true)
        val fathersName: String?,

        @Column(name = "IMIE_MATKI", length = 40, nullable = true)
        val mothersName: String?,

        @Column(name ="NAZWISKO_PANIEN_MATKI", length = 40, nullable = true)
        val mothersMaidenName: String?,

        @Column(name = "NAZWISKO_RODOWE", length = 40, nullable = true)
        val familyName: String?,

        @Column(name = "SZKOLA", length = 100, nullable = true)
        val school: String?,

        @Column(name = "SR_NR_DOWODU", length = 20, nullable = true)
        val idNumber: String?,

        @Column(name = "NIP", length = 13, nullable = true)
        val nip: String?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "WKU_KOD", referencedColumnName = "KOD", nullable = true)
        val wku: Wku,

        @Column(name = "KAT_WOJSKOWA")
        val militaryCategory: String?,

        @Column(name = "STOSUNEK_WOJSKOWY")
        val militaryRelationship: String?,

        @Column(name = "UWAGI")
        val comments: String?,

        @Column(name = "WWW")
        val www: String?,

        @Email
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

        @Column(name = "UTW_ID", nullable = false)
        val creatorOracleUser: String,

        @Column(name = "UTW_DATA", nullable = false)
        val creationDate: Date,

        @Column(name = "MOD_ID", nullable = false)
        val ModificationOracleUser: String,

        @Column(name = "MOD_DATA", nullable = false)
        val modificationDate: Date,

        @Column(name = "PLEC", nullable = false, length = 1)
        val sex: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYTUL_PRZED", nullable = true)
        val titlePrefix: Title?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYTUL_PO", nullable = true)
        val titleSuffix: Title?,

        @Column(name = "CZY_POLONIA", length = 1)
        val isPolish: String?,

        @Column(name = "ZAMIEJSCOWA", length = 1)
        val nonresident: String?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "GDZIE_SOCJALNE", referencedColumnName = "KOD", nullable = true)
        val socialBenefitsSource: OrganizationalUnit?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "US_ID", referencedColumnName = "ID", nullable = true)
        val taxOffice: TaxOffice?,

        @Column(name = "AKAD_CZY_REZERWA", length = 1, nullable = false)
        val dormitoryReserve: String,

        @Column(name = "AKAD_WYKROCZENIA", length = 1000, nullable = true)
        val dormitoryOffense: String?,

        @Column(name = "AKAD_UWAGI", length = 1000, nullable = true)
        val dormitoryComments: String?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
        val middleSchool: School?,

        @Column(name = "TYP_DOKUMENTU", length = 1, nullable = true)
        val documentType: String?,

        @Column(name = "NR_KARTY_BIBL", length = 30, nullable = true)
        val libraryCardNumber: String?,

        @Email
        @Column(name = "BK_EMAIL", length = 100, nullable = true)
        val carrersOfficeEmail: String?,

        @Column(name = "BK_CZYMIGROWAC", length = 2, precision = 0, nullable = false)
        val carrersOfficeMigrate: Int?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRAJ_URODZENIA", referencedColumnName = "KOD", nullable = true)
        val birthCountry: Citizenship?,

        @Column(name = "DANE_ZEW_STATUS", length = 1, nullable = false)
        val externalDataStatus: String,

        @Lob
        @Column(name = "OSIAGNIECIA", nullable = true)
        val achievements: String?,

        @Lob
        @Column(name = "OSIAGNIECIA_ANG", nullable = true)
        val achievementsEng: String?,

        @Column(name = "EPUAP_IDENTYFIKATOR", length = 64, nullable = true)
        val epuapId: String?,

        @Column(name = "EPUAP_SKRYTKA", length = 64, nullable = true)
        val epuapSafe: String?,

        @Column(name = "CZY_LOSY_ABSOLWENTOW", length = 1, nullable = false)
        val isGraduateFate: String,

        @Column(name = "CZY_LOSY_ABS_ZGODA", nullable = true)
        val isGraduateFateDate: Date?,

        @Column(name = "CZY_LOSY_ABS_REZYG", nullable = true)
        val isGraduateFateResignDate: Date?,

        @Column(name = "BK_CZY_MIGR_ZGODA", nullable = true)
        val carrersOfficeMigrateApproval: Date?,

        @Column(name = "BK_CZY_MIGR_REZYG", nullable = true)
        val carrersOfficeMigrateResignation: Date?,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRAJ_DOK_KOD", referencedColumnName = "KOD", nullable = true)
        val identityDocumentIssuerCountry: Citizenship?,

        @Column(name = "DATA_WAZNOSCI_DOWODU", nullable = true)
        val identityDocumentExpirationDate: Date?,

        @Column(name = "CZY_KLUB_ABS", length = 1, nullable = false)
        val graduateClubJoinApproval: String?,

        @Column(name = "CZY_KLUB_ABS_ZGODA", nullable = true)
        val graduateClubJoinApprovalDate: Date?,

        @Column(name = "CZY_KLUB_ABS_REZYG", nullable = true)
        val graduateClubJoinResignDate: Date?
        )