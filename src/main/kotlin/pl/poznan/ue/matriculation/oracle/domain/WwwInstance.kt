package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

//@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_INSTALACJE_WWW")
data class WwwInstance(
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

//        @Column(name = "MOD_DATA", nullable = false)
//        val modificationDate: Date,
//
//        @Column(name = "MOD_ID", length = 30, nullable = false)
//        val modificationUser: String,
//
//        @Column(name = "UTW_DATA", nullable = false)
//        val creationDate: Date,
//
//        @Column(name = "UTW_ID", length = 30, nullable = false)
//        val creationUser: String,

        @JsonIgnore
        @OneToMany(mappedBy = "wwwInstance", fetch = FetchType.LAZY)
        val organizationalUnits: Set<OrganizationalUnit>

//        @Column(name = "INST_WWW_KONTA")
//        val
) {
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