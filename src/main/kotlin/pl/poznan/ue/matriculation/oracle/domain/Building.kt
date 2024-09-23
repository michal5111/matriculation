package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import java.util.*

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_BUDYNKI")
class Building(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "NAZWA", length = 100, nullable = false)
    val name: String,

    @Column(name = "SZEROKOSC_GEO", nullable = true)
    val latitude: Int? = null,

    @Column(name = "DLUGOSC_GEO", nullable = true)
    val longitude: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KAMP_KOD", referencedColumnName = "KOD")
    val campus: Campus,

    @Column(name = "NAZWA_ANG", length = 100, nullable = true)
    val englishName: String? = null,

    @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
    val display: Char,

    @Column(name = "KOD_ERP", length = 50, nullable = true)
    val erpCode: String? = null,

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "FOTO_BLOB_ID", referencedColumnName = "ID")
//    val photoBlob: Blob,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_OD", nullable = true)
    val dateFrom: Date? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_DO", nullable = true)
    val dateTO: Date? = null,

    @OneToOne(mappedBy = "building", fetch = FetchType.LAZY)
    val address: Address,

    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    val phoneNumbers: List<PhoneNumber>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Building

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
