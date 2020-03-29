package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_PROGRAMY")
data class Programme(

        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        var description: String,

        @Column(name = "DATA_OD", nullable = false)
        var dateFrom: Date,

        @Column(name = "DATA_DO", nullable = true)
        var dateTo: Date? = null,

        @Column(name = "TRYB_STUDIOW", length = 100, nullable = true)
        var studyMode: String? = null,

        @Column(name = "RODZAJ_STUDIOW", length = 100, nullable = true)
        var typeOfStudies: String? = null,

        @Column(name = "CZAS_TRWANIA", length = 100, nullable = true)
        var duration: String? = null,

        @Column(name = "DESCRIPTION", length = 200, nullable = true)
        var descriptionEng: String? = null,

        @Column(name = "DALSZE_STUDIA", length = 200, nullable = true)
        var furtherStudies: String? = null,

        @Column(name = "DALSZE_STUDIA_ANG", length = 200, nullable = true)
        var furtherStudiesEng: String? = null,

        @Column(name = "RODZAJ_STUDIOW_ANG", length = 100, nullable = true)
        var typeOfStudiesEng: String? = null,

        @Column(name = "CZAS_TRWANIA_ANG", length = 100, nullable = true)
        var durationEng: String? = null,

        @Column(name = "TRYB_STUDIOW_ANG", length = 100, nullable = true)
        var studyModeEng: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TCDYD_KOD", referencedColumnName = "KOD", nullable = true)
        var didacticCycleType: DidacticCycleType? = null,

        @Column(name = "LICZBA_JEDN", length = 10, nullable = false)
        var unitsNumber: Long? = null,

        @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
        var display: Char = 'T',

        @Column(name = "UPRAWNIENIA_ZAWODOWE", nullable = true)
        @Lob
        var professionalQualifications: String? = null,

        @Column(name = "UPRAWNIENIA_ZAWODOWE_ANG", nullable = true)
        @Lob
        var professionalQualificationsEng: String? = null,

        @Column(name = "OPIS_NIE", length = 200, nullable = true)
        var descriptionGer: String? = null,

        @Column(name = "OPIS_ROS", length = 200, nullable = true)
        var descriptionRus: String? = null,

        @Column(name = "OPIS_HIS", length = 200, nullable = true)
        var descriptionHis: String? = null,

        @Column(name = "OPIS_FRA", length = 200, nullable = true)
        var descriptionFra: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KONF_SR_KOD", referencedColumnName = "KOD")
        var mediumConfiguration: MediumConfiguration,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PROW_KIER_ID", referencedColumnName = "ID")
        var conductedFieldOfStudy: ConductedFieldOfStudy,

        @Column(name = "PROFIL", length = 1, nullable = true)
        var profile: Char? = null,

        @Column(name = "CZY_STUDIA_MIEDZYOBSZAROWE", length = 1, nullable = false)
        var isInternationalStudies: Char = 'N',

        @Column(name = "CZY_BEZPLATNY_USTAWA")
        var isFreeAct: Char? = null,

        @Column(name = "LIMIT_ECTS", length = 15, nullable = true)
        var ectsLimit: Double? = null,

        @Column(name = "DODATKOWE_ECTS_USTAWA", length = 15, nullable = false)
        var additionalEctsAct: Double = 0.0,

        @Column(name = "DODATKOWE_ECTS_UCZELNIA", length = 15, nullable = false)
        var additionalEctsUniversity: Double = 0.0,

        @Column(name = "CZYNNIKI_SZKODLIWE", length = 4000, nullable = true)
        var harmfulFactors: String? = null,

        @Column(name = "ZAKRES", length = 500, nullable = true)
        var range: String? = null,

        @Column(name = "ZAKRES_ANG", length = 500, nullable = true)
        var rangeEng: String? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD_PODST", referencedColumnName = "KOD", nullable = true)
        var organizationalUnitPrimary: OrganizationalUnit? = null,

        @Column(name = "KOD_POLON_ISM", length = 20, nullable = true)
        var polonCodeIsm: String? = null,

        @Column(name = "KOD_POLON_DR", length = 20, nullable = true)
        var polonCodeDr: String? = null,

        @ManyToOne
        @JoinColumn(name = "KOD_ISCED", referencedColumnName = "KOD", nullable = true)
        var iscedCode: IscedCode? = null,

        @Column(name = "KOD_POLON_REKRUTACJA", length = 20, nullable = true)
        var polonCodeRegistration: String? = null,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD_PROW", referencedColumnName = "KOD", nullable = true)
        var organizationalUnitLeading: OrganizationalUnit? = null,

        @Column(name = "USTAL_DATE_KONCA_STUDIOW", length = 1, nullable = false)
        var setEndOfStudyDate: Char = 'D',

        @Column(name = "UID_POLON_DR", length = 128, nullable = true)
        var polonUidDr: String? = null,

        @OneToMany(mappedBy = "programme", fetch = FetchType.LAZY)
        val personProgrammes: MutableList<PersonProgramme>,

        @OneToMany(mappedBy = "programme", fetch = FetchType.LAZY)
        val programmeStages: MutableList<ProgrammeStage>
)