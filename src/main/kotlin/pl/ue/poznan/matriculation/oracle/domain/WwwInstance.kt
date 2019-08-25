package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.validator.constraints.URL
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_INSTALACJE_WWW")
data class WwwInstance(
        @Id
        @Column(name = "KOD")
        val code: String,

        @URL
        @Column(name = "URL")
        val url: String,

        @Column(name = "HOSTNAME")
        val hostname: String?,

        @Column(name = "DATABASENAME")
        val dataBaseName: String?,

        @Column(name = "INST_WWW_ID")
        val wwwInstanceId: Int,

        @Column(name = "MOD_DATA")
        val modificationDate: Date,

        @Column(name = "MOD_ID")
        val modificationUser: String,

        @Column(name = "UTW_DATA")
        val creationDate: Date,

        @Column(name = "UTW_ID")
        val creationUser: String,

        @JsonIgnore
        @OneToMany(mappedBy = "wwwInstance", fetch = FetchType.LAZY)
        val organizationalUnits: Set<OrganizationalUnit>

//        @Column(name = "INST_WWW_KONTA")
//        val
        )