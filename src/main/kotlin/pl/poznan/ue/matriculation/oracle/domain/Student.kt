package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_STUDENCI")
class Student(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_ST_SEQ")
        @SequenceGenerator(sequenceName = "DZ_ST_SEQ", allocationSize = 1, name = "DZ_ST_SEQ")
        @Column(name = "ID", nullable = false, updatable = false, length = 10)
        val id: Long? = null,

        @Column(name = "INDEKS", length = 30, nullable = false)
        var indexNumber: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
        val organizationalUnit: OrganizationalUnit,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TYP_IND_KOD", referencedColumnName = "KOD")
        val indexType: IndexType,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false)
        var person: Person,

        @Column(name = "INDEKS_GLOWNY", length = 1, nullable = false)
        var mainIndex: Char,

        @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
        val personProgrammes: MutableList<PersonProgramme> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Student

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}