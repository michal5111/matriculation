package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_GMINY")
class Commune(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "NAZWA", length = 100, nullable = false)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PW_KOD", referencedColumnName = "KOD", nullable = false)
    var county: County,

    @OneToMany(mappedBy = "commune", fetch = FetchType.LAZY)
    var address: MutableList<Address>,

    @OneToMany(mappedBy = "commune", fetch = FetchType.LAZY)
    var postalCodes: MutableList<PostalCode>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Commune

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}