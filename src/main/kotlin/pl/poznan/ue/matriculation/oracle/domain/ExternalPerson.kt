package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*

@Entity
@Table(name = "DZ_OSOBY_ZEWNETRZNE")
class ExternalPerson(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_OSZ_SEQ")
    @SequenceGenerator(sequenceName = "DZ_OSZ_SEQ", allocationSize = 1, name = "DZ_OSZ_SEQ")
    @Column(name = "ID", nullable = false, updatable = false, length = 10)
    val id: Long? = null,

    @Column(name = "IMIE", length = 40, nullable = false)
    var name: String,

    @Column(name = "NAZWISKO", length = 40, nullable = false)
    var surname: String,

    @Column(name = "EMAIL")
    var email: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
    var school: School? = null,

    @Column(name = "UWAGI", nullable = true, length = 2000)
    var comments: String?,

    @Column(name = "PLEC", nullable = false, length = 1)
    var sex: Char,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYTUL_PRZED", nullable = true)
    val titlePrefix: Title? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYTUL_PO", nullable = true)
    val titleSuffix: Title? = null,

    @Column(name = "CZY_WYSWIETLAC", nullable = false, length = 1)
    val display: Char,

    @Column(name = "EMAIL2")
    var email2: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_ZEW_ID", referencedColumnName = "ID", nullable = true)
    val externalOrganizationalUnit: ExternalOrganizationalUnit?,

    @OneToMany(
        mappedBy = "coordinatorExternalPerson",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var externalPersonContracts: MutableList<Contract> = mutableListOf(),

    @OneToMany(
        mappedBy = "coordinatorExternalPerson",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var externalPersonCooperations: MutableList<Cooperation> = mutableListOf()
) : BaseEntity()
