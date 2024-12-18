package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "DZ_TRYBY_STUD_PRGOS")
class PersonProgrammeSourceOfFinancing(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_TRYB_ST_PRGOS_SEQ")
    @SequenceGenerator(sequenceName = "DZ_TRYB_ST_PRGOS_SEQ", allocationSize = 1, name = "DZ_TRYB_ST_PRGOS_SEQ")
    @Column(name = "ID", nullable = false, updatable = false, length = 10)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID")
    var personProgramme: PersonProgramme,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRYB_ODB_ST_KOD", referencedColumnName = "KOD")
    val sourceOfFinancing: SourceOfFinancing,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_OD", nullable = false)
    val dateFrom: LocalDate,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_DO", nullable = true)
    val dateTo: LocalDate? = null
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonProgrammeSourceOfFinancing

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
