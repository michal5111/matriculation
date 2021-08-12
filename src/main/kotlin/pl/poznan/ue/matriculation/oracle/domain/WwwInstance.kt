package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
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

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_INST_WWW_ID_SEQ")
    @SequenceGenerator(sequenceName = "DZ_INST_WWW_ID_SEQ", allocationSize = 1, name = "DZ_INST_WWW_ID_SEQ")
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