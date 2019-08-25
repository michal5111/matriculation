package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_URZEDY_SKARBOWE")
data class TaxOffice(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_US_SEQ")
        @SequenceGenerator(sequenceName = "DZ_US_SEQ", allocationSize = 1, name = "DZ_US_SEQ")
        @Column(name = "ID")
        val id: Long,

        @Column(name = "NAZWA")
        val name: String,

        @Column(name = "KOD")
        val code: String,

        @Column(name = "MOD_DATA")
        val modificationDate: Date,

        @Column(name = "MOD_ID")
        val modificationUser: String,

        @Column(name = "UTW_DATA")
        val creationDate: Date,

        @Column(name = "UTW_ID")
        val creationUser: String,

        @JsonIgnore
        @OneToMany(mappedBy = "taxOffice", fetch = FetchType.LAZY)
        val persons: Set<Person>
)