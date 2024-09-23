package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy

@Entity
//@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "DZ_SZKOLY")
class School(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_SZK_SEQ")
    @SequenceGenerator(sequenceName = "DZ_SZK_SEQ", allocationSize = 1, name = "DZ_SZK_SEQ")
    @Column(name = "ID")
    val id: Long? = null,

    @Column(name = "NAZWA", length = 200, nullable = false)
    val name: String,

    @Column(name = "RODZAJ_SZKOLY", length = 2, nullable = false)
    val schoolType: String,

    @Column(name = "STATUS_PRAWNY", length = 1000, nullable = true)
    val legalStatus: String?,

    @Column(name = "STATUS_PRAWNY_ANG", length = 1000, nullable = true)
    val legalStatusEng: String?,

    @Column(name = "KOD_SZK", length = 20, nullable = true)
    val schoolCode: String?,

    @Column(name = "WWW", length = 100, nullable = true)
    val www: String?,

    @Column(name = "KODERASMUS", length = 20, nullable = true)
    val erasmusCode: String?,

    @Column(name = "NAME", length = 200, nullable = true)
    val nameEng: String?,

    @Column(name = "KOD_POLON", length = 20, nullable = true)
    val polonCode: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_NADRZ_ID", referencedColumnName = "ID", nullable = true)
    val parentSchool: School?,

    @Column(name = "KOD_HR", length = 50, nullable = true)
    val hrCode: String?,

    @Column(name = "SKALA_OCEN", length = 2000, nullable = true)
    val gradingScale: String?,

    @Column(name = "SKALA_OCEN_ANG", length = 2000, nullable = true)
    val gradingScaleEng: String?,

    @Column(name = "EMAIL", length = 100, nullable = true)
    val email: String?,

    @Column(name = "PIC", length = 9, nullable = true)
    val PIC: Int?,

    @Column(name = "CZY_PODMIOT_PUBLICZNY", length = 1, nullable = true)
    val isPublicEntity: String?,

    @Column(name = "CZY_NON_PROFIT", length = 1, nullable = true)
    val isNonProfit: String?,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "LOGO_BLOB_ID", referencedColumnName = "ID", nullable = true)
//    val logoBlob: Blob?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REG_NUTS_KOD", referencedColumnName = "KOD", nullable = true)
    val nutsRegion: NUTSRegion?,

    @Column(name = "SCHAC", length = 100, nullable = true)
    val schac: String?,

    @Column(name = "INFOKARTA_ID_BLOBBOX", length = 20, nullable = true)
    val factSheetId: String?,

    @Column(name = "INFOKARTA_UWAGI", length = 200, nullable = true)
    val factSheetComments: String?,

    @Column(name = "INFOKARTA_WWW", length = 2000, nullable = true)
    val factSheetWWW: String?,

    @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
    val display: String,

    @Column(name = "EMAIL_NOMINACJE", length = 1000, nullable = true)
    val emailNominations: String?,

    @OneToMany(mappedBy = "middleSchool", fetch = FetchType.LAZY)
    var persons: MutableList<Person>,

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    var entitlementDocuments: MutableList<EntitlementDocument>,

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    var contracts: MutableList<Contract>,

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    var externalPersons: MutableList<Person>,

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    var externalOrganizationalUnit: MutableList<ExternalOrganizationalUnit>,

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    var arrivals: MutableList<Arrival>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as School

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
