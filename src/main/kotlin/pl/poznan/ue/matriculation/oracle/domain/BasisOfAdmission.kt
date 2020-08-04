package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_PODSTAWY_PODJECIA_STUDIOW")
class BasisOfAdmission(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 100, nullable = false)
        val description: String,

        @Column(name = "OPIS_ANG", length = 200, nullable = true)
        val descriptionEng: String? = null,

        @Column(name = "CZY_AKTUALNA", length = 1, nullable = false)
        val isCurrent: Char = 'T',

        @OneToMany(mappedBy = "basisOfAdmission", fetch = FetchType.LAZY)
        val personProgrammeBasisOfAdmission: MutableList<PersonProgrammeBasisOfAdmission>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasisOfAdmission

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}