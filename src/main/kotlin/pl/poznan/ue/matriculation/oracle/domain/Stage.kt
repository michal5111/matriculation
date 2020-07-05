package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_ETAPY")
class Stage(

        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        val description: String,

        @Column(name = "DESCRIPTION", length = 200, nullable = true)
        val descriptionEng: String? = null,

        @OneToMany(mappedBy = "stage", fetch = FetchType.LAZY)
        val programmeStages: MutableList<ProgrammeStage>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stage

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}