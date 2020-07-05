package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_PODSTAWY_STUDIOW_PRGOS")
class PersonProgrammeBasisOfAdmission(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_PODST_STUD_PRGOS_SEQ")
        @SequenceGenerator(sequenceName = "DZ_PODST_STUD_PRGOS_SEQ", allocationSize = 1, name = "DZ_PODST_STUD_PRGOS_SEQ")
        @Column(name = "ID", nullable = false, updatable = false, length = 10)
        val id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID")
        val personProgramme: PersonProgramme,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PODST_PODJ_STD_KOD", referencedColumnName = "KOD")
        val basisOfAdmission: BasisOfAdmission,

        @Column(name = "DATA_OD", nullable = false)
        val dateFrom: Date,

        @Column(name = "DATA_DO", nullable = true)
        val dateTo: Date? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonProgrammeBasisOfAdmission

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}