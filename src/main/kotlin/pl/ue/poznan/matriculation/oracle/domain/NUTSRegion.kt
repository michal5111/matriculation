package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

//@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_REGIONY_NUTS")
data class NUTSRegion(
        @Id
        @NotBlank
        @Column(name = "KOD" ,length = 10, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        val description: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OB_KOD", referencedColumnName = "KOD", nullable = false)
        val citizenship: Citizenship,

        @Column(name = "POZIOM", length = 1, nullable = false)
        val level: String,

        @Column(name = "MOD_DATA", nullable = false)
        val modificationDate: Date,

        @Column(name = "MOD_ID", length = 30, nullable = false)
        val modificationUser: String,

        @Column(name = "UTW_DATA", nullable = false)
        val creationDate: Date,

        @Column(name = "UTW_ID", length = 30, nullable = false)
        val creationUser: String,

        @JsonIgnore
        @OneToMany(mappedBy = "nutsRegion", fetch = FetchType.LAZY)
        val schools: Set<School>
        )
