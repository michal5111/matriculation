package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_INSTALACJE_WWW")
class WwwInstance(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "URL", length = 2000, nullable = false)
    val url: String,

    @Column(name = "HOSTNAME", length = 50, nullable = true)
    val hostname: String?,

    @Column(name = "DATABASENAME", length = 50, nullable = true)
    val dataBaseName: String?,

    @Column(name = "INST_WWW_ID", nullable = false)
    val wwwInstanceId: Int,

    @OneToMany(mappedBy = "wwwInstance", fetch = FetchType.LAZY)
    val organizationalUnits: Set<OrganizationalUnit>

//        @Column(name = "INST_WWW_KONTA")
//        val
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WwwInstance

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
