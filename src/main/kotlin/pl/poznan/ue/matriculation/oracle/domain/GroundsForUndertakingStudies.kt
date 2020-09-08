package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "DZ_PODSTAWY_PODJECIA_STUDIOW")
class GroundsForUndertakingStudies(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        val description: String,

        @Column(name = "OPIS_ANG", length = 200, nullable = true)
        val descriptionEng: String?,

        @Column(name = "CZY_AKTUALNA", length = 1, nullable = false)
        val isCurrent: Char = 'T'

//        @OneToMany(mappedBy = "groundsForUndertakingStudies", fetch = FetchType.LAZY)
//        val personProgrammes: MutableList<PersonProgramme>
) {
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