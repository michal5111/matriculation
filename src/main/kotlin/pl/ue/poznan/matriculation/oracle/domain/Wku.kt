package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

//@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_WKU")
data class Wku(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "NAZWA", length = 100, nullable = false)
        val name: String,

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
        @OneToMany(mappedBy = "wku", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
        val persons: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "wku", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
        val addresses: List<Address>
        )