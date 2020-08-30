package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_KODY_ISCED")
class IscedCode(
        @Id
        @Column(name = "KOD", length = 5, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        var description: String,

        @Column(name = "OPIS_ANG", length = 200, nullable = false)
        var descriptionEng: String,

        @OneToMany(mappedBy = "iscedCode", fetch = FetchType.LAZY)
        val programmes: MutableList<Programme>,

        @OneToMany(mappedBy = "iscedCode", fetch = FetchType.LAZY)
        val personProgrammes: MutableList<PersonProgramme>,

        @OneToMany(mappedBy = "iscedCode", fetch = FetchType.LAZY)
        val arrivals: MutableList<Arrival>
) {
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