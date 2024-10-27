package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import java.time.LocalDate

@Entity
@Table(name = "DZ_ADRESY")
class Address(
    @Id
    @GeneratedValue(generator = "addressIdGenerator")
    @GenericGenerator(
        name = "addressIdGenerator",
        parameters = [Parameter(name = "sequenceId", value = "DZ_ADR_SEQ")],
        strategy = "pl.poznan.ue.matriculation.oracle.customKeyGenerator.SequenceStringKeyGenerator"
    )
    @Column(name = "ID", length = 10)
    val id: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = true)
    var person: Person? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WKU_KOD", referencedColumnName = "KOD", nullable = true)
    var wku: Wku? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD", nullable = true)
    var organizationalUnit: OrganizationalUnit? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TADR_KOD", referencedColumnName = "KOD", nullable = true)
    var addressType: AddressType,

    @Column(name = "ULICA", length = 80, nullable = true)
    var street: String? = null,

    @Column(name = "MIASTO", length = 60, nullable = true)
    var city: String? = null,

    @Column(name = "KOD_POCZ", length = 5, nullable = true)
    var zipCode: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PW_KOD", referencedColumnName = "KOD", nullable = true)
    var county: County? = null,

    @Column(name = "NR_DOMU", length = 80, nullable = true)
    var houseNumber: String? = null,

    @Column(name = "NR_LOKALU", length = 80, nullable = true)
    var flatNumber: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AKADEMIKI_ID", referencedColumnName = "ID", nullable = true)
    var dormitory: Dormitory? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUD_KOD", referencedColumnName = "KOD", nullable = true)
    var building: Building? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "US_ID", referencedColumnName = "ID", nullable = true)
    var taxOffice: TaxOffice? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GM_KOD", referencedColumnName = "KOD", nullable = true)
    var commune: Commune? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
    var school: School? = null,

    @Column(name = "KOD_ZAGR", length = 20, nullable = true)
    var foreignZipCode: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OB_KOD", referencedColumnName = "KOD", nullable = true)
    var country: Citizenship? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOST_ID", referencedColumnName = "ID", nullable = true)
    var warehouseSuppliers: WarehouseSuppliers? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_OD", nullable = true)
    var dateFrom: LocalDate? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_DO", nullable = true)
    var dateTo: LocalDate? = null,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_MIASTO", length = 1, nullable = true)
    var cityIsCity: Boolean?
) : BaseEntity()
