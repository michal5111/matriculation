package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_KAMPUSY")
class Campus(
        @Id
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 200, nullable = false)
        var description: String,

//        @Column(name = "UTW_ID", nullable = false)
//        val creatorOracleUser: String? = null,
//
//        @Column(name = "UTW_DATA", nullable = false)
//        val creationDate: Date? = null,
//
//        @Column(name = "MOD_ID", nullable = false)
//        val modificationOracleUser: String? = null,
//
//        @Column(name = "MOD_DATA", nullable = false)
//        val modificationDate: Date? = null,

        @OneToMany(mappedBy = "campus")
        var buildings: List<Building>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Campus

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}