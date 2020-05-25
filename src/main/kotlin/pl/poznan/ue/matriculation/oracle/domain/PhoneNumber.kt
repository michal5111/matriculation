package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "DZ_TELEFONY")
class PhoneNumber(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_TEL_SEQ")
        @SequenceGenerator(sequenceName = "DZ_TEL_SEQ", allocationSize = 1, name = "DZ_TEL_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

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

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = true)
        var person: Person? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "AKADEMIKI_ID", referencedColumnName = "ID", nullable = true)
        var dormitory: Dormitory? = null,

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

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "FRM_KOD", referencedColumnName = "KOD", nullable = true)
        val company: Company? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
        val school: School? = null
) {
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