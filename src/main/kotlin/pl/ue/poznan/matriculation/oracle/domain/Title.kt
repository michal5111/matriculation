package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

//@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_TYTULY")
data class Title(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_TYT_SEQ")
        @SequenceGenerator(sequenceName = "DZ_TYT_SEQ", allocationSize = 1, name = "DZ_TYT_SEQ")
        @Column(name = "ID")
        val id: Long,

        @NotBlank
        @Column(name = "NAZWA", length = 30, nullable = false)
        val name: String,

        @Column(name = "OPIS", length = 200, nullable = true)
        val description: String?,

        @Column(name = "KOD", length = 20, nullable = true)
        val code: String?,

        @Column(name = "KOD_POLON", length = 20, nullable = true)
        val polonCode: String?,

        @Column(name = "MOD_DATA", nullable = false)
        val modificationDate: Date,

        @Column(name = "MOD_ID", length = 30, nullable = false)
        val modificationUser: String,

        @Column(name = "UTW_DATA", nullable = false)
        val creationDate: Date,

        @Column(name = "UTW_ID", length = 30, nullable = false)
        val creationUser: String,

        @JsonIgnore
        @OneToMany(mappedBy = "titlePrefix", fetch = FetchType.LAZY)
        val personsPrefixes: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "titleSuffix", fetch = FetchType.LAZY)
        val personsSuffixes: Set<Person>
        )