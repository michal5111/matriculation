package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_FIRMY")
class Company(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "NAZWA", length = 400, nullable = false)
    val name: String,

    @Column(name = "NAZWA_ANG", length = 400, nullable = true)
    val englishName: String? = null,

    @Column(name = "NIP", length = 13, nullable = true)
    val nip: String? = null,

    @Column(name = "MODUL", length = 10, nullable = true)
    val module: String? = null,

    @Column(name = "TYP_DZIALANOSCI", length = 1, nullable = true)
    val typeOfActivity: Char? = null,

    @Column(name = "FORMA_PRAWNA", length = 3, nullable = true)
    val legalForm: String? = null,

    @Column(name = "NUMER_KRS", length = 10, nullable = true)
    val krsNumber: String? = null,

    @Column(name = "REGON", length = 14, nullable = true)
    val regon: String? = null,

    @Column(name = "ADRES_WWW", length = 200, nullable = true)
    val website: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRM_KOD", referencedColumnName = "KOD", nullable = true)
    val company: Company? = null,

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    val phoneNumbers: List<PhoneNumber>

) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Company

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
