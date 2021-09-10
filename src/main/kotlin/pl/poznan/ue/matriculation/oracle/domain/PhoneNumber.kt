package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import javax.persistence.*

@Entity
@Table(name = "DZ_TELEFONY")
class PhoneNumber(
    @Id
    @GeneratedValue(generator = "phoneNumberIdGenerator")
    @GenericGenerator(
        name = "phoneNumberIdGenerator",
        parameters = [Parameter(name = "sequenceId", value = "DZ_TEL_SEQ")],
        strategy = "pl.poznan.ue.matriculation.oracle.customKeyGenerator.SequenceStringKeyGenerator"
    )
    @Column(name = "ID", length = 10)
    val id: String? = null,

    @Column(name = "NUMER", length = 50, nullable = false)
    var number: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TTEL_KOD", referencedColumnName = "KOD")
    var phoneNumberType: PhoneNumberType,

    @Column(name = "Uwagi", length = 100, nullable = true)
    var comments: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUD_KOD", referencedColumnName = "KOD", nullable = true)
    var building: Building? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD", nullable = true)
    var organizationalUnit: OrganizationalUnit? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = true)
    var person: Person? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AKADEMIKI_ID", referencedColumnName = "ID", nullable = true)
    var dormitory: Dormitory? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRM_KOD", referencedColumnName = "KOD", nullable = true)
    val company: Company? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
    val school: School? = null
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhoneNumber

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
