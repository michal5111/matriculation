package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*

@Entity
//@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "DZ_TYTULY")
class Title(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_TYT_SEQ")
    @SequenceGenerator(sequenceName = "DZ_TYT_SEQ", allocationSize = 1, name = "DZ_TYT_SEQ")
    @Column(name = "ID")
    val id: Long? = null,

    @Column(name = "NAZWA", length = 30, nullable = false)
    val name: String,

    @Column(name = "OPIS", length = 200, nullable = true)
    val description: String?,

    @Column(name = "KOD", length = 20, nullable = true)
    val code: String?,

    @Column(name = "KOD_POLON", length = 20, nullable = true)
    val polonCode: String?,

    @OneToMany(mappedBy = "titlePrefix", fetch = FetchType.LAZY)
    val personsPrefixes: Set<Person>,

    @OneToMany(mappedBy = "titleSuffix", fetch = FetchType.LAZY)
    val personsSuffixes: Set<Person>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Title

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
