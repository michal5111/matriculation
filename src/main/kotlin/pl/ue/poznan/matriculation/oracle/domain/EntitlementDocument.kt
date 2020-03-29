package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_DOKUMENTY_UPR")
data class EntitlementDocument(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_DOK_UPR_SEQ")
        @SequenceGenerator(sequenceName = "DZ_DOK_UPR_SEQ", allocationSize = 1, name = "DZ_DOK_UPR_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @JsonIgnore
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false)
        var person: Person? = null,

        @Column(name = "RODZAJ", length = 1, nullable = false)
        var type: Char,

        @Column(name = "NUMER", length = 200, nullable = false)
        var number: String,

        @Column(name = "DATA_WYSTAWIENIA", nullable = false)
        var issueDate: Date,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
        var school: School? = null,

        @Column(name = "OPIS", length = 500, nullable = true)
        var description: String? = null,

        @OneToMany(mappedBy = "entitlementDocument", fetch = FetchType.LAZY)
        val personProgrammeS: MutableList<PersonProgramme> = mutableListOf()
)