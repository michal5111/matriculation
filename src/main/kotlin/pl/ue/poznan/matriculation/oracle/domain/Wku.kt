package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_WKU")
data class Wku(
        @Id
        @Column(name = "KOD")
        val code: String,

        @Column(name = "NAZWA")
        val name: String,

        @Column(name = "MOD_DATA")
        val modificationDate: Date,

        @Column(name = "MOD_ID")
        val modificationUser: String,

        @Column(name = "UTW_DATA")
        val creationDate: Date,

        @Column(name = "UTW_ID")
        val creationUser: String,

        @JsonIgnore
        @OneToMany(mappedBy = "wku", fetch = FetchType.LAZY)
        val persons: Set<Person>
        )