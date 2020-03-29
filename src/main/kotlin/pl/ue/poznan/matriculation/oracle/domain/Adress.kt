package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_ADRESY")
data class Address(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_ADR_SEQ")
        @SequenceGenerator(sequenceName = "DZ_ADR_SEQ", allocationSize = 1, name = "DZ_ADR_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @JsonIgnore
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
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

//        @Column(name = "UTW_ID", nullable = false)
//        val creatorOracleUser: String? = null,
//
//        @Column(name = "UTW_DATA", nullable = false)
//        val creationDate: Date? = null,
//
//        @Column(name = "MOD_ID", nullable = false)
//        val modificationOracleUser: String? = null,
//
//        @Column(name = "MOD_DATA", nullable = false)
//        val modificationDate: Date? = null,

        @Column(name = "NR_DOMU", length = 80, nullable = true)
        var houseNumber: String? = null,

        @Column(name = "NR_LOKALU", length = 80, nullable = true)
        var apartmentNumber: String? = null,

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "AKADEMIKI_ID", referencedColumnName = "ID", nullable = true)
        var dormitory: Dormitory? = null,

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "BUD_KOD", referencedColumnName = "KOD", nullable = true)
        var building: Building? = null,

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "US_ID", referencedColumnName = "ID", nullable = true)
        var taxOffice: TaxOffice? = null,

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "GM_KOD", referencedColumnName = "KOD", nullable = true)
        var commune: Commune? = null,

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
        var school: School? = null,

        @Column(name = "KOD_ZAGR", length = 20, nullable = true)
        var foreignZipCode: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OB_KOD", referencedColumnName = "KOD", nullable = true)
        var countryCode: Citizenship? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DOST_ID", referencedColumnName = "ID", nullable = true)
        var warehouseSuppliers: WarehouseSuppliers? = null,

        @Column(name = "DATA_OD", nullable = true)
        var dateFrom: Date? = null,

        @Column(name = "DATA_DO", nullable = true)
        var dateTo: Date? = null,

        @Column(name = "CZY_MIASTO", length = 1, nullable = true)
        var cityIsCity: Char
)