package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import java.time.LocalDate

@Entity
//@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "DZ_JEDNOSTKI_ORGANIZACYJNE")
class OrganizationalUnit(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TJEDN_KOD", referencedColumnName = "KOD", nullable = false)
    val unitType: UnitType,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @Column(name = "OPIS_ANG", length = 200, nullable = true)
    val descriptionEng: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD", nullable = true)
    val organizationalUnit: OrganizationalUnit?,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_DYDAKTYCZNA", length = 1, nullable = false)
    val isDidactic: Boolean,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_ZATRUDNIA", length = 1, nullable = true)
    val doesEmploys: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INST_WWW_KOD", referencedColumnName = "KOD", nullable = true)
    val wwwInstance: WwwInstance?,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_PRZYZNAJE_AKADEMIKI", length = 1, nullable = false)
    val isGrantingDorms: Boolean,

    @Column(name = "SKROT_NAZWY", length = 20, nullable = true)
    val nameAbbreviation: String?,

    @Column(name = "OPIS_DO_SUPLEMENTU", length = 2000, nullable = true)
    val supplementDescription: String?,

    @Column(name = "OPIS_DO_SUPLEMENTU_ANG", length = 2000, nullable = true)
    val supplementDescriptionEng: String?,

    @Column(name = "ADRES_WWW", length = 200, nullable = true)
    val wwwAddress: String?,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
    val display: Boolean,

    @Column(name = "OPIS_NIE", length = 200, nullable = true)
    val germanDescription: String?,

    @Column(name = "OPIS_ROS", length = 200, nullable = true)
    val russianDescription: String?,

    @Column(name = "OPIS_HIS", length = 200, nullable = true)
    val hispanicDescription: String?,

    @Column(name = "OPIS_FRA", length = 200, nullable = true)
    val frenchDescription: String?,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_ARCHIWIZUJE", length = 1, nullable = false)
    val isArchiving: Boolean,

    @Column(name = "ARCH_NR_DOPLYWU_S", length = 10, nullable = true)
    val archInflowNumberS: Int?,

    @Column(name = "ARCH_NR_DOPLYWU_D", length = 10, nullable = true)
    val archInflowNumberD: Int?,

    @Column(name = "ARCH_NR_DOPLYWU_H", length = 10, nullable = true)
    val archInflowNumberH: Int?,

    @Column(name = "ARCH_NR_DOPLYWU_P", length = 10, nullable = true)
    val archInflowNumberP: Int?,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_ZALOZENIA", nullable = true)
    val dateOfEstablishment: LocalDate?,

    @Column(name = "REGON", length = 14, nullable = true)
    val regon: String?,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_ZAMIEJSCOWA", length = 1, nullable = false)
    val isForeign: Boolean,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_PODSTAWOWA", length = 1, nullable = false)
    val isBasic: Boolean,

    @Column(name = "KOD_POLON", length = 50, nullable = true)
    val polonCode: String?,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ZDJECIE_BLOB_ID", referencedColumnName = "ID", nullable = true)
//    val pictureBlob: Blob?,
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "LOGO_BLOB_ID", referencedColumnName = "ID", nullable = true)
//    val logoBlob: Blob?,

    @Column(name = "MPK", length = 100, nullable = true)
    val mpk: String?,

    @Column(name = "SKROT_DO_JRWA", length = 20, nullable = true)
    val jrwaAbbreviation: String?,

    @Column(name = "EMAIL", length = 100, nullable = true)
    val email: String?,

    @Column(name = "INFOKARTA_ID_BLOBBOX", length = 20, nullable = true)
    val factSheetBlobBoxId: String?,

    @Column(name = "INFOKARTA_UWAGI", length = 200, nullable = true)
    val factSheetComments: String?,

    @Column(name = "GUID", length = 30, nullable = false)
    val guid: String,

    @Column(name = "UID_POLON", length = 128, nullable = true)
    val polonUid: String?,

    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
    val persons: Set<Person>,

    @OneToMany(mappedBy = "socialBenefitsSource", fetch = FetchType.LAZY)
    val socialBenefitsSourcePersons: Set<Person>,

    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
    val phoneNumbers: List<PhoneNumber>,

    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
    val conductedFieldOfStudy: MutableList<ConductedFieldOfStudy>,

    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
    val fieldOfStudyPermissions: MutableList<FieldOfStudyPermission>,

    @OneToMany(mappedBy = "organizationalUnitPrimary", fetch = FetchType.LAZY)
    val programmesPrimary: MutableList<Programme>,

    @OneToMany(mappedBy = "organizationalUnitLeading", fetch = FetchType.LAZY)
    val programmesLeading: MutableList<Programme>,

    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
    val personProgrammes: MutableList<PersonProgramme>,

    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
    val folders: MutableList<Folder>,

    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
    val cooperations: MutableList<Cooperation>,

    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
    val clausesAndRegulations: MutableList<ClauseAndRegulation>,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrganizationalUnit

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
