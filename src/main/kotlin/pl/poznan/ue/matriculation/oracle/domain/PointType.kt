package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_TYPY_PUNKTOW")
class PointType(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = true)
    var description: String,

    @Column(name = "DESCRIPTION", length = 100, nullable = true)
    var descriptionEng: String,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pointType")
    var mediumConfigurations: MutableList<MediumConfiguration>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PointType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}