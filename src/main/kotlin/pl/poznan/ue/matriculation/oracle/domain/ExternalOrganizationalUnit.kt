package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_JEDNOSTKI_ORGANIZACYJNE_ZEW")
class ExternalOrganizationalUnit(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_JED_ORG_ZEW_SEQ")
    @SequenceGenerator(sequenceName = "DZ_JED_ORG_ZEW_SEQ", allocationSize = 1, name = "DZ_JED_ORG_ZEW_SEQ")
    @Column(name = "ID", nullable = false, updatable = false, length = 10)
    val id: Long? = null,

    @Column(name = "NAZWA", nullable = false, length = 200)
    val name: String,

    @Column(name = "ADRES_WWW", nullable = true, length = 200)
    val url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
    val school: School? = null,

    @Column(name = "UWAGI")
    val comments: String?,

    @Column(name = "INFOKARTA_ID_BLOBBOX", length = 20, nullable = true)
    val factSheetBlobBoxId: String?,

    @Column(name = "INFOKARTA_UWAGI", length = 200, nullable = true)
    val factSheetComments: String?,

    @Column(name = "INFOKARTA_WWW", length = 2000, nullable = true)
    val factSheetUrl: String?,

    @Column(name = "NAZWA_ANG", nullable = true, length = 200)
    val nameEng: String?,

    @Column(name = "ID_EWP")
    val ewpID: String?,

    @OneToMany(mappedBy = "externalOrganizationalUnit", fetch = FetchType.LAZY)
    val externalPersons: Set<ExternalPerson>
) : BaseEntity()
