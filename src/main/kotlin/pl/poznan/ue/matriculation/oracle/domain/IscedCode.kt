package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_KODY_ISCED")
class IscedCode(
    @Id
    @Column(name = "KOD", length = 5, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @Column(name = "OPIS_ANG", length = 200, nullable = false)
    val descriptionEng: String,

    @OneToMany(mappedBy = "iscedCode", fetch = FetchType.LAZY)
    val programmes: MutableList<Programme>,

    @OneToMany(mappedBy = "iscedCode", fetch = FetchType.LAZY)
    val personProgrammes: MutableList<PersonProgramme>,

    @OneToMany(mappedBy = "iscedCode", fetch = FetchType.LAZY)
    val arrivals: MutableList<Arrival>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IscedCode

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
