package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*

@Entity
@Table(name = "DZ_STUDENCI")
data class Student(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_ST_SEQ")
        @SequenceGenerator(sequenceName = "DZ_ST_SEQ", allocationSize = 1, name = "DZ_ST_SEQ")
        @Column(name = "ID", nullable = false, updatable = false, length = 10)
        val id: Long? = null,

        @Column(name = "INDEKS", length = 30, nullable = false)
        var indexNumber: String,

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
        val organizationalUnit: OrganizationalUnit,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYP_IND_KOD", referencedColumnName = "KOD")
        val indexType: IndexType,

        @JsonIgnore
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false)
        var person: Person,

        @Column(name = "INDEKS_GLOWNY", length = 1, nullable = false)
        var mainIndex: Char,

        @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
        val personProgrammes: MutableList<PersonProgramme> = mutableListOf()
)