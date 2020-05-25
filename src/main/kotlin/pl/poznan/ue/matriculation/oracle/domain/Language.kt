package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_JEZYKI")
class Language(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "NAZWA", length = 30, nullable = false)
        var name: String,

        @Column(name = "NAZWA_ANG", length = 100, nullable = false)
        var nameEng: String,

        @Column(name = "KOD_ISO6391", length = 2, nullable = true)
        var Iso6391Code: String? = name,

        @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
        var conductedFieldsOfStudy: MutableList<ConductedFieldOfStudy>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Language

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}