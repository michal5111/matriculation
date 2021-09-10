package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import pl.poznan.ue.matriculation.oracle.jpaConverters.TAndNToBooleanConverter
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_PODSTAWY_PODJECIA_STUDIOW")
class GroundsForUndertakingStudies(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @Column(name = "OPIS_ANG", length = 200, nullable = true)
    val descriptionEng: String?,

    @Convert(converter = TAndNToBooleanConverter::class)
    @Column(name = "CZY_AKTUALNA", length = 1, nullable = false)
    val isCurrent: Boolean = true

//        @OneToMany(mappedBy = "groundsForUndertakingStudies", fetch = FetchType.LAZY)
//        val personProgrammes: MutableList<PersonProgramme>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroundsForUndertakingStudies

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
