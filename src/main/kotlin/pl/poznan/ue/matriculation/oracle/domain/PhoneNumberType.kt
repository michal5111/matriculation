package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_TYPY_TELEFONOW")
class PhoneNumberType(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = false)
    var description: String,

    @Column(name = "CZY_DOMYSLNY", length = 1, nullable = false)
    var isDefault: Char,

    @OneToMany(mappedBy = "phoneNumberType")
    var phoneNumbers: List<PhoneNumber>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhoneNumberType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}